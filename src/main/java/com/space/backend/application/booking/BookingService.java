package com.space.backend.application.booking;

import com.space.backend.domain.booking.CancelPreview;

import java.util.UUID;

public interface BookingService {
    BookingResponse createBooking(UUID userId, CreateBookingCommand command);
    BookingListResponse getMyBookings(UUID userId);
    BookingResponse getBookingById(UUID userId, UUID bookingId);
    CancelPreview getCancelPreview(UUID userId, UUID bookingId);
    void requestCancel(UUID userId, UUID bookingId);
}
