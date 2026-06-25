package com.space.backend.application.space;

import com.space.backend.domain.space.SpaceOperatingHours;

import java.time.LocalTime;

public record OperatingHoursDto(
        int dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean isClosed
) {
    public static OperatingHoursDto from(SpaceOperatingHours h) {
        return new OperatingHoursDto(h.getDayOfWeek(), h.getOpenTime(), h.getCloseTime(), h.isClosed());
    }
}
