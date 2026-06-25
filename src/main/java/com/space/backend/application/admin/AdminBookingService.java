package com.space.backend.application.admin;

import com.space.backend.application.booking.BookingListResponse;

import java.util.UUID;

public interface AdminBookingService {
    AdminBookingListResponse getBookings(BookingSearchCondition condition);
    AdminBookingDetailResponse getBookingById(UUID bookingId);
    void updateBookingStatus(UUID bookingId, UpdateBookingStatusCommand command);
    void approveRefund(UUID bookingId, ApproveRefundCommand command);
}
