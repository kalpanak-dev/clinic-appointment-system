package com.clinic;

import com.clinic.service.SlotService;
import com.clinic.web.Dtos.SlotDto;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class SlotServiceTest {
    private final SlotService service = new SlotService(9, 17, 30);
    private final LocalDateTime longAgo = LocalDateTime.of(2020, 1, 1, 0, 0);

    @Test
    void weekdayHasSixteenHalfHourSlots() {
        List<SlotDto> slots = service.slotsFor(LocalDate.of(2030, 1, 7), Set.of(), longAgo); // a Monday
        assertEquals(16, slots.size());
        assertTrue(slots.stream().allMatch(SlotDto::available));
    }

    @Test
    void bookedSlotIsNotAvailable() {
        LocalDateTime nine = LocalDateTime.of(2030, 1, 7, 9, 0);
        List<SlotDto> slots = service.slotsFor(LocalDate.of(2030, 1, 7), Set.of(nine), longAgo);
        assertFalse(slots.get(0).available());
        assertTrue(slots.get(1).available());
    }

    @Test
    void weekendHasNoSlots() {
        assertTrue(service.slotsFor(LocalDate.of(2030, 1, 5), Set.of(), longAgo).isEmpty()); // Saturday
    }

    @Test
    void validatesSlotStarts() {
        assertTrue(service.isValidSlotStart(LocalDateTime.of(2030, 1, 7, 16, 30)));
        assertFalse(service.isValidSlotStart(LocalDateTime.of(2030, 1, 7, 17, 0)));  // closing time
        assertFalse(service.isValidSlotStart(LocalDateTime.of(2030, 1, 7, 9, 15)));  // not aligned
        assertFalse(service.isValidSlotStart(LocalDateTime.of(2030, 1, 5, 10, 0)));  // Saturday
    }
}
