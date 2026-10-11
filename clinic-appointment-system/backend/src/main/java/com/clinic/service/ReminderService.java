package com.clinic.service;

import com.clinic.model.Appointment;
import com.clinic.model.AppointmentStatus;
import com.clinic.repo.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Emails patients a reminder once, when their appointment is less than 24 hours away. */
@Service
public class ReminderService {
    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);
    private final AppointmentRepository appointments;
    private final JavaMailSender mail;
    private final String from;

    public ReminderService(AppointmentRepository appointments, JavaMailSender mail,
                           @Value("${app.mail-from}") String from) {
        this.appointments = appointments;
        this.mail = mail;
        this.from = from;
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        for (Appointment a : appointments.findByStatusAndReminderSentFalseAndStartTimeBetween(
                AppointmentStatus.BOOKED, now, now.plusHours(24))) {
            try {
                SimpleMailMessage m = new SimpleMailMessage();
                m.setFrom(from);
                m.setTo(a.getPatient().getEmail());
                m.setSubject("Appointment reminder");
                m.setText("Hello " + a.getPatient().getName() + ",\n\nThis is a reminder of your appointment with Dr. "
                        + a.getDoctor().getUser().getName() + " on "
                        + a.getStartTime().format(DateTimeFormatter.ofPattern("EEE d MMM yyyy 'at' HH:mm")) + ".");
                mail.send(m);
                a.setReminderSent(true);
            } catch (Exception e) {
                log.warn("Could not send reminder for appointment {}: {}", a.getId(), e.getMessage());
            }
        }
    }
}
