package com.clinic.web;

import com.clinic.service.AppointmentService;
import com.clinic.web.Dtos.AppointmentDto;
import com.clinic.web.Dtos.BookRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) { this.service = service; }

    private static String role(Authentication auth) {
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    @PostMapping
    public AppointmentDto book(Authentication auth, @Valid @RequestBody BookRequest req) {
        return service.book(auth.getName(), req);
    }

    /** Patients see their own appointments, doctors their schedule, admins everything. */
    @GetMapping("/mine")
    public List<AppointmentDto> mine(Authentication auth) { return service.mine(auth.getName(), role(auth)); }

    @PatchMapping("/{id}/cancel")
    public AppointmentDto cancel(Authentication auth, @PathVariable Long id) {
        return service.cancel(id, auth.getName(), role(auth));
    }

    @PatchMapping("/{id}/complete")
    public AppointmentDto complete(Authentication auth, @PathVariable Long id) {
        return service.complete(id, auth.getName(), role(auth));
    }
}
