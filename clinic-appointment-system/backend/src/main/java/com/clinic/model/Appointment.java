package com.clinic.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * slotKey is "doctorId|startTime" while the appointment is BOOKED and null once cancelled.
 * The UNIQUE constraint on it makes double booking impossible even under concurrent requests.
 */
@Entity
@Getter @Setter
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Doctor doctor;
    @ManyToOne(optional = false)
    private User patient;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.BOOKED;
    private String reason;
    private boolean reminderSent = false;
    @Column(unique = true)
    private String slotKey;
}
