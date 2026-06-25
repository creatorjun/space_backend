package com.space.backend.application.admin;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.booking.PaymentType;

import java.time.Instant;
import java.util.UUID;

public record AdminBookingDto(
        UUID id,
        UUID userId,
        String userName,
        UUID spaceId,
        String spaceName,
        Instant startAt,
        Instant endAt,
        int hours,
        int headcount,
        int totalPrice,
        PaymentType paymentType,
        BookingStatus status,
        String memo,
        String adminMemo,
        Instant createdAt
) {
    public static AdminBookingDto from(Booking b) {
        return new AdminBookingDto(
                b.getId(),
                b.getUser().getId(),
                b.getUser().getName(),
                b.getSpace().getId(),
                b.getSpace().getName(),
                b.getStartAt(), b.getEndAt(),
                b.getHours(), b.getHeadcount(), b.getTotalPrice(),
                b.getPaymentType(), b.getStatus(),
                b.getMemo(), b.getAdminMemo(),
                b.getCreatedAt()
        );
    }
}
