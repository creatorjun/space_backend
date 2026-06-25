package com.space.backend.presentation.admin;

import com.space.backend.domain.booking.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull BookingStatus status,
        String adminMemo
) {}
