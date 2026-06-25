// src/main/java/com/space/backend/infrastructure/persistence/booking/AdminBookingQueryAdapter.java
package com.space.backend.infrastructure.persistence.booking;

import com.space.backend.application.admin.BookingSearchCondition;
import com.space.backend.domain.booking.AdminBookingQueryPort;
import com.space.backend.domain.booking.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminBookingQueryAdapter implements AdminBookingQueryPort {

    private final AdminBookingQueryRepository queryRepository;

    @Override
    public List<Booking> findByCondition(BookingSearchCondition condition) {
        return queryRepository.findByCondition(condition);
    }

    @Override
    public long countByCondition(BookingSearchCondition condition) {
        return queryRepository.countByCondition(condition);
    }
}
