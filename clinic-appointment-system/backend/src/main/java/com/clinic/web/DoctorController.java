package com.clinic.web;

import com.clinic.repo.DoctorRepository;
import com.clinic.service.AppointmentService;
import com.clinic.web.Dtos.DoctorDto;
import com.clinic.web.Dtos.SlotDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorRepository doctors;
    private final AppointmentService service;

    public DoctorController(DoctorRepository doctors, AppointmentService service) {
        this.doctors = doctors;
        this.service = service;
    }

    @GetMapping
    public List<DoctorDto> list() { return doctors.findAll().stream().map(DoctorDto::from).toList(); }

    @GetMapping("/{id}/slots")
    public List<SlotDto> slots(@PathVariable Long id,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.slotsForDoctor(id, date);
    }
}
