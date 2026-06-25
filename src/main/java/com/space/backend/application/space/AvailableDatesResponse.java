package com.space.backend.application.space;

import java.time.LocalDate;
import java.util.Map;

public record AvailableDatesResponse(
        int year,
        int month,
        Map<LocalDate, Boolean> dates
) {}
