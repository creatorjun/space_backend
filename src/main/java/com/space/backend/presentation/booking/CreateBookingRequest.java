package com.space.backend.presentation.booking;

import com.space.backend.application.booking.CreateBookingCommand;
import com.space.backend.domain.booking.PaymentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID spaceId,
        @NotNull Instant startAt,
        @Min(1) int hours,
        @Min(1) int headcount,
        PaymentType paymentType,
        String memo
) {
    public CreateBookingCommand toCommand() {
        return new CreateBookingCommand(spaceId, startAt, hours, headcount, paymentType, memo);
    }
}
