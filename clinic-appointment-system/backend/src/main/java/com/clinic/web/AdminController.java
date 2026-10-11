package com.clinic.web;

import com.clinic.model.Doctor;
import com.clinic.model.Role;
import com.clinic.model.User;
import com.clinic.repo.DoctorRepository;
import com.clinic.repo.UserRepository;
import com.clinic.web.Dtos.DoctorDto;
import com.clinic.web.Dtos.DoctorRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final DoctorRepository doctors;
    private final PasswordEncoder encoder;

    public AdminController(UserRepository users, DoctorRepository doctors, PasswordEncoder encoder) {
        this.users = users;
        this.doctors = doctors;
        this.encoder = encoder;
    }

    @PostMapping("/doctors")
    @Transactional
    public DoctorDto addDoctor(@Valid @RequestBody DoctorRequest r) {
        if (users.existsByEmail(r.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        User u = new User();
        u.setEmail(r.email());
        u.setName(r.name());
        u.setPassword(encoder.encode(r.password()));
        u.setRole(Role.DOCTOR);
        users.save(u);
        Doctor d = new Doctor();
        d.setUser(u);
        d.setSpecialty(r.specialty());
        return DoctorDto.from(doctors.save(d));
    }
}
