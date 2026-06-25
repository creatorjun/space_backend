// src/main/java/com/space/backend/application/booking/BookingService.java
package com.space.backend.application.booking;

import com.space.backend.domain.booking.CancelPreview;

import java.util.UUID;

public interface BookingService {
    BookingResult createBooking(UUID userId, CreateBookingCommand command);
    BookingListResult getMyBookings(UUID userId);
    BookingResult getBookingById(UUID userId, UUID bookingId);
    CancelPreview getCancelPreview(UUID userId, UUID bookingId);
    void requestCancel(UUID userId, UUID bookingId);
}
