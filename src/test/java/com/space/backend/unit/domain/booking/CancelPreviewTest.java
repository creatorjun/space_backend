package com.space.backend.unit.domain.booking;

import com.space.backend.domain.booking.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class CancelPreviewTest {

    private final BookingDomainService service = new BookingDomainService();

    private Booking buildBooking(long hoursUntilStart, int totalPrice) {
        return Booking.builder()
                .status(BookingStatus.CONFIRMED)
                .startAt(Instant.now().plus(Duration.ofHours(hoursUntilStart)))
                .endAt(Instant.now().plus(Duration.ofHours(hoursUntilStart + 2)))
                .totalPrice(totalPrice)
                .hours(2)
                .headcount(1)
                .build();
    }

    @Test
    @DisplayName("48시간 이상 남으면 전액 환불")
    void fullRefund() {
        CancelPreview p = service.calculateCancelPreview(buildBooking(72, 50000), Instant.now());
        assertThat(p.refundAmount()).isEqualTo(50000);
        assertThat(p.penaltyAmount()).isZero();
        assertThat(p.isCancellable()).isTrue();
    }

    @Test
    @DisplayName("24시간 이상 48시간 미만 — 50% 위약금")
    void halfPenalty() {
        CancelPreview p = service.calculateCancelPreview(buildBooking(30, 60000), Instant.now());
        assertThat(p.refundAmount()).isEqualTo(30000);
        assertThat(p.penaltyAmount()).isEqualTo(30000);
    }

    @Test
    @DisplayName("24시간 미만 — 100% 위약금")
    void fullPenalty() {
        CancelPreview p = service.calculateCancelPreview(buildBooking(12, 40000), Instant.now());
        assertThat(p.refundAmount()).isZero();
        assertThat(p.penaltyAmount()).isEqualTo(40000);
        assertThat(p.isCancellable()).isTrue();
    }

    @Test
    @DisplayName("이용 시작 후 — 취소 불가")
    void notCancellable() {
        CancelPreview p = service.calculateCancelPreview(buildBooking(-1, 30000), Instant.now());
        assertThat(p.isCancellable()).isFalse();
    }
}
