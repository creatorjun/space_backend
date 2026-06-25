package com.space.backend.infrastructure.scheduler;

import com.space.backend.application.booking.BookingExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpiryScheduler {

    private final BookingExpiryService bookingExpiryService;

    @Scheduled(fixedDelay = 60_000)
    public void run() {
        try {
            bookingExpiryService.expireStaleBookings();
        } catch (Exception e) {
            log.error("[BookingExpiryScheduler] 실행 중 오류 발생", e);
        }
    }
}
