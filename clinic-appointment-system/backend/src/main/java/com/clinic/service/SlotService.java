package com.clinic.service;

import com.clinic.web.Dtos.SlotDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Generates bookable time slots: weekdays, between opening and closing hour, in fixed-length slots. */
@Component
public class SlotService {
    private final int openHour;
    private final int closeHour;
    private final int slotMinutes;

    public SlotService(@Value("${clinic.open-hour}") int openHour,
                       @Value("${clinic.close-hour}") int closeHour,
                       @Value("${clinic.slot-minutes}") int slotMinutes) {
        this.openHour = openHour;
        this.closeHour = closeHour;
        this.slotMinutes = slotMinutes;
    }

    public int slotMinutes() { return slotMinutes; }

    public List<SlotDto> slotsFor(LocalDate date, Set<LocalDateTime> booked, LocalDateTime now) {
        List<SlotDto> slots = new ArrayList<>();
        if (isWeekend(date)) return slots;
        LocalDateTime start = date.atTime(openHour, 0);
        LocalDateTime close = date.atTime(closeHour, 0);
        while (!start.plusMinutes(slotMinutes).isAfter(close)) {
            boolean free = !booked.contains(start) && start.isAfter(now);
            slots.add(new SlotDto(start, start.plusMinutes(slotMinutes), free));
            start = start.plusMinutes(slotMinutes);
        }
        return slots;
    }

    public boolean isValidSlotStart(LocalDateTime start) {
        if (isWeekend(start.toLocalDate())) return false;
        if (start.getSecond() != 0 || start.getNano() != 0) return false;
        LocalTime t = start.toLocalTime();
        int minutesFromOpen = (t.getHour() - openHour) * 60 + t.getMinute();
        int total = (closeHour - openHour) * 60;
        return minutesFromOpen >= 0 && minutesFromOpen % slotMinutes == 0 && minutesFromOpen + slotMinutes <= total;
    }

    private boolean isWeekend(LocalDate d) {
        return d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
