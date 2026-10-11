package com.clinic.web;

import com.clinic.model.Appointment;
import com.clinic.model.Doctor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class Dtos {
    public record RegisterRequest(@Email @NotBlank String email, @Size(min = 6) String password, @NotBlank String name) {}
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String email, String name, String role) {}

    public record DoctorRequest(@Email @NotBlank String email, @Size(min = 6) String password,
                                @NotBlank String name, @NotBlank String specialty) {}
    public record DoctorDto(Long id, String name, String specialty) {
        public static DoctorDto from(Doctor d) { return new DoctorDto(d.getId(), d.getUser().getName(), d.getSpecialty()); }
    }

    public record SlotDto(LocalDateTime start, LocalDateTime end, boolean available) {}
    public record BookRequest(@NotNull Long doctorId, @NotNull LocalDateTime start, String reason) {}

    public record AppointmentDto(Long id, Long doctorId, String doctorName, String specialty, String patientName,
                                 String patientEmail, LocalDateTime start, LocalDateTime end, String status, String reason) {
        public static AppointmentDto from(Appointment a) {
            return new AppointmentDto(a.getId(), a.getDoctor().getId(), a.getDoctor().getUser().getName(),
                    a.getDoctor().getSpecialty(), a.getPatient().getName(), a.getPatient().getEmail(),
                    a.getStartTime(), a.getEndTime(), a.getStatus().name(), a.getReason());
        }
    }
}
