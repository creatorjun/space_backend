package com.space.backend.application.space;

import com.space.backend.domain.booking.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public record AvailableSlotsResponse(
        LocalDate date,
        List<TimeSlot> slots
) {}
