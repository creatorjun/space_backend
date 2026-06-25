// src/main/java/com/space/backend/presentation/booking/BookingResponse.java
package com.space.backend.presentation.booking;

import com.space.backend.application.booking.BookingResult;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.booking.PaymentType;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID spaceId,
        String spaceName,
        String spaceThumbnailUrl,
        Instant startAt,
        Instant endAt,
        int hours,
        int headcount,
        int totalPrice,
        PaymentType paymentType,
        BookingStatus status,
        Instant pendingExpiresAt,
        String memo,
        Instant createdAt
) {
    public static BookingResponse from(BookingResult result) {
        return new BookingResponse(
                result.id(),
                result.spaceId(),
                result.spaceName(),
                result.spaceThumbnailUrl(),
                result.startAt(),
                result.endAt(),
                result.hours(),
                result.headcount(),
                result.totalPrice(),
                result.paymentType(),
                result.status(),
                result.pendingExpiresAt(),
                result.memo(),
                result.createdAt()
        );
    }
}
