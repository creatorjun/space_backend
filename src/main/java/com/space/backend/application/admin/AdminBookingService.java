// src/main/java/com/space/backend/application/admin/AdminBookingService.java
package com.space.backend.application.admin;

import java.util.UUID;

public interface AdminBookingService {
    AdminBookingListResult getBookings(BookingSearchCondition condition);
    AdminBookingResult getBookingById(UUID bookingId);
    void updateBookingStatus(UUID bookingId, UpdateBookingStatusCommand command);
    void approveRefund(UUID bookingId, ApproveRefundCommand command);
}
