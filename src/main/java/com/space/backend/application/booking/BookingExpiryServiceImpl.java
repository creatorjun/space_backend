package com.space.backend.application.booking;

import com.space.backend.domain.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingExpiryServiceImpl implements BookingExpiryService {

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public void expireStaleBookings() {
        int count = bookingRepository.bulkExpire(Instant.now());
        if (count > 0) {
            log.info("[BookingExpiry] {}건 예약 만료 처리", count);
        }
    }
}
