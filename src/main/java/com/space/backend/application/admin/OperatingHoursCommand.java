package com.space.backend.application.admin;

import java.time.LocalTime;

public record OperatingHoursCommand(
        int dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isClosed
) {}
