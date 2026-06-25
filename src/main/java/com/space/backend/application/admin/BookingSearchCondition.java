package com.space.backend.application.admin;

import com.space.backend.domain.booking.BookingStatus;

import java.time.LocalDate;

public record BookingSearchCondition(
        BookingStatus status,
        LocalDate date,
        int page,
        int size
) {}
