package com.space.backend.application.booking;

import com.space.backend.domain.booking.PaymentType;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingCommand(
        UUID spaceId,
        Instant startAt,
        int hours,
        int headcount,
        PaymentType paymentType,
        String memo
) {}
