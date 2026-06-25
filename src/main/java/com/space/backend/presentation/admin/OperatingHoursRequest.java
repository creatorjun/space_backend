package com.space.backend.presentation.admin;

import com.space.backend.application.admin.OperatingHoursCommand;

import java.time.LocalTime;

public record OperatingHoursRequest(
        int dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isClosed
) {
    public OperatingHoursCommand toCommand() {
        return new OperatingHoursCommand(dayOfWeek, openTime, closeTime, isClosed);
    }
}
