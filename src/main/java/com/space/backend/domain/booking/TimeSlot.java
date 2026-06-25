package com.space.backend.domain.booking;

import java.time.LocalTime;

public record TimeSlot(
        LocalTime startTime,
        LocalTime endTime,
        boolean available
) {}
