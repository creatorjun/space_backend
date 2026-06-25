// src/main/java/com/space/backend/domain/booking/AdminBookingQueryPort.java
package com.space.backend.domain.booking;

import com.space.backend.application.admin.BookingSearchCondition;

import java.util.List;

public interface AdminBookingQueryPort {
    List<Booking> findByCondition(BookingSearchCondition condition);
    long countByCondition(BookingSearchCondition condition);
}
