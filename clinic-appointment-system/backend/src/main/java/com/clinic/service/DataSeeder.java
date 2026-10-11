package com.clinic.service;

import com.clinic.model.Doctor;
import com.clinic.model.Role;
import com.clinic.model.User;
import com.clinic.repo.DoctorRepository;
import com.clinic.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Demo data: admin@clinic.com / admin123 and two doctors (password doctor123). Change before real use. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final DoctorRepository doctors;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, DoctorRepository doctors, PasswordEncoder encoder) {
        this.users = users;
        this.doctors = doctors;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByEmail("admin@clinic.com")) {
            create("admin@clinic.com", "Admin", "admin123", Role.ADMIN);
        }
        if (doctors.count() == 0) {
            addDoctor("dr.silva@clinic.com", "Nimal Silva", "Cardiology");
            addDoctor("dr.perera@clinic.com", "Kumari Perera", "Dermatology");
        }
    }

    private User create(String email, String name, String pw, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPassword(encoder.encode(pw));
        u.setRole(role);
        return users.save(u);
    }

    private void addDoctor(String email, String name, String specialty) {
        Doctor d = new Doctor();
        d.setUser(create(email, name, "doctor123", Role.DOCTOR));
        d.setSpecialty(specialty);
        doctors.save(d);
    }
}
