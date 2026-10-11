package com.clinic.repo;

import com.clinic.model.Appointment;
import com.clinic.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdAndStatusAndStartTimeBetween(Long doctorId, AppointmentStatus status,
                                                                 LocalDateTime from, LocalDateTime to);
    List<Appointment> findByPatientEmailOrderByStartTime(String email);
    List<Appointment> findByDoctorUserEmailOrderByStartTime(String email);
    List<Appointment> findAllByOrderByStartTime();
    List<Appointment> findByStatusAndReminderSentFalseAndStartTimeBetween(AppointmentStatus status,
                                                                          LocalDateTime from, LocalDateTime to);
}
