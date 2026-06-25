// src/main/java/com/space/backend/presentation/admin/AdminBookingResponse.java
package com.space.backend.presentation.admin;

import com.space.backend.application.admin.AdminBookingResult;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.booking.PaymentType;

import java.time.Instant;
import java.util.UUID;

public record AdminBookingResponse(
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
    public static AdminBookingResponse from(AdminBookingResult result) {
        return new AdminBookingResponse(
                result.id(), result.userId(), result.userName(),
                result.spaceId(), result.spaceName(),
                result.startAt(), result.endAt(),
                result.hours(), result.headcount(), result.totalPrice(),
                result.paymentType(), result.status(),
                result.memo(), result.adminMemo(), result.createdAt()
        );
    }
}
