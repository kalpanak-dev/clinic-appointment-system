package com.clinic.service;

import com.clinic.model.*;
import com.clinic.repo.AppointmentRepository;
import com.clinic.repo.DoctorRepository;
import com.clinic.repo.UserRepository;
import com.clinic.web.Dtos.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final DoctorRepository doctors;
    private final UserRepository users;
    private final SlotService slots;

    public AppointmentService(AppointmentRepository appointments, DoctorRepository doctors,
                              UserRepository users, SlotService slots) {
        this.appointments = appointments;
        this.doctors = doctors;
        this.users = users;
        this.slots = slots;
    }

    @Transactional(readOnly = true)
    public List<SlotDto> slotsForDoctor(Long doctorId, LocalDate date) {
        if (!doctors.existsById(doctorId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found");
        Set<LocalDateTime> booked = appointments
                .findByDoctorIdAndStatusAndStartTimeBetween(doctorId, AppointmentStatus.BOOKED,
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .stream().map(Appointment::getStartTime).collect(Collectors.toSet());
        return slots.slotsFor(date, booked, LocalDateTime.now());
    }

    @Transactional
    public AppointmentDto book(String patientEmail, BookRequest req) {
        User patient = users.findByEmail(patientEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (patient.getRole() != Role.PATIENT)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients can book appointments");
        Doctor doctor = doctors.findById(req.doctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        if (!slots.isValidSlotStart(req.start()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid appointment slot");
        if (!req.start().isAfter(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book a time in the past");

        Appointment a = new Appointment();
        a.setDoctor(doctor);
        a.setPatient(patient);
        a.setStartTime(req.start());
        a.setEndTime(req.start().plusMinutes(slots.slotMinutes()));
        a.setReason(req.reason());
        a.setSlotKey(doctor.getId() + "|" + req.start());
        try {
            // The UNIQUE constraint on slotKey guarantees no double booking, even for simultaneous requests.
            return AppointmentDto.from(appointments.saveAndFlush(a));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This slot has just been booked. Please pick another.");
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> mine(String email, String role) {
        List<Appointment> list = switch (role) {
            case "ADMIN" -> appointments.findAllByOrderByStartTime();
            case "DOCTOR" -> appointments.findByDoctorUserEmailOrderByStartTime(email);
            default -> appointments.findByPatientEmailOrderByStartTime(email);
        };
        return list.stream().map(AppointmentDto::from).toList();
    }

    @Transactional
    public AppointmentDto cancel(Long id, String email, String role) {
        Appointment a = find(id);
        boolean allowed = "ADMIN".equals(role)
                || a.getPatient().getEmail().equals(email)
                || a.getDoctor().getUser().getEmail().equals(email);
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (a.getStatus() != AppointmentStatus.BOOKED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only booked appointments can be cancelled");
        a.setStatus(AppointmentStatus.CANCELLED);
        a.setSlotKey(null); // frees the slot for someone else
        return AppointmentDto.from(a);
    }

    @Transactional
    public AppointmentDto complete(Long id, String email, String role) {
        Appointment a = find(id);
        boolean allowed = "ADMIN".equals(role) || a.getDoctor().getUser().getEmail().equals(email);
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (a.getStatus() != AppointmentStatus.BOOKED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only booked appointments can be completed");
        a.setStatus(AppointmentStatus.COMPLETED);
        return AppointmentDto.from(a);
    }

    private Appointment find(Long id) {
        return appointments.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
