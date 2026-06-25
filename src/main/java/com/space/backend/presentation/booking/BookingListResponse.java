// src/main/java/com/space/backend/presentation/booking/BookingListResponse.java
package com.space.backend.presentation.booking;

import com.space.backend.application.booking.BookingListResult;

import java.util.List;

public record BookingListResponse(List<BookingResponse> bookings) {
    public static BookingListResponse from(BookingListResult result) {
        return new BookingListResponse(
                result.bookings().stream().map(BookingResponse::from).toList()
        );
    }
}
