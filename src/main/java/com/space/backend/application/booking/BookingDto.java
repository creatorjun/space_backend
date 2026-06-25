package com.space.backend.application.booking;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.booking.PaymentType;

import java.time.Instant;
import java.util.UUID;

public record BookingDto(
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
    public static BookingDto from(Booking b) {
        return new BookingDto(
                b.getId(),
                b.getSpace().getId(),
                b.getSpace().getName(),
                b.getSpace().getThumbnailUrl(),
                b.getStartAt(),
                b.getEndAt(),
                b.getHours(),
                b.getHeadcount(),
                b.getTotalPrice(),
                b.getPaymentType(),
                b.getStatus(),
                b.getPendingExpiresAt(),
                b.getMemo(),
                b.getCreatedAt()
        );
    }
}
