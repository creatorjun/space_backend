package com.space.backend.unit.domain.booking;

import com.space.backend.domain.booking.*;
import com.space.backend.domain.space.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class BookingDomainServiceTest {

    private BookingDomainService service;
    private Space space;

    @BeforeEach
    void setUp() {
        service = new BookingDomainService();

        SpaceCategory cat = SpaceCategory.builder().name("test").build();

        SpaceOperatingHours mon = SpaceOperatingHours.builder()
                .dayOfWeek(1).openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0)).isClosed(false).build();
        SpaceOperatingHours sun = SpaceOperatingHours.builder()
                .dayOfWeek(0).isClosed(true).build();

        space = Space.builder()
                .category(cat)
                .name("Test Space")
                .capacity(10)
                .minHours(1)
                .maxHours(8)
                .pricePerHour(10000)
                .build();
    }

    @Test
    @DisplayName("예약 없는 날자 → 가능")
    void availableDates_noBookings() {
        Map<LocalDate, Boolean> result = service.calculateAvailableDates(
                space, 2030, 6, List.of());
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("취소 미리보기 — 48시간 이상 남으면 전액 환불")
    void cancelPreview_fullRefund() {
        Booking booking = Booking.builder()
                .status(BookingStatus.CONFIRMED)
                .startAt(Instant.now().plus(Duration.ofHours(72)))
                .endAt(Instant.now().plus(Duration.ofHours(74)))
                .totalPrice(30000)
                .hours(2)
                .headcount(1)
                .build();
        CancelPreview preview = service.calculateCancelPreview(booking, Instant.now());
        assertThat(preview.refundAmount()).isEqualTo(30000);
        assertThat(preview.penaltyAmount()).isZero();
        assertThat(preview.isCancellable()).isTrue();
    }

    @Test
    @DisplayName("취소 미리보기 — 24~48시간 사이 → 50% 위약금")
    void cancelPreview_halfPenalty() {
        Booking booking = Booking.builder()
                .status(BookingStatus.CONFIRMED)
                .startAt(Instant.now().plus(Duration.ofHours(36)))
                .endAt(Instant.now().plus(Duration.ofHours(38)))
                .totalPrice(40000)
                .hours(2)
                .headcount(1)
                .build();
        CancelPreview preview = service.calculateCancelPreview(booking, Instant.now());
        assertThat(preview.refundAmount()).isEqualTo(20000);
        assertThat(preview.penaltyAmount()).isEqualTo(20000);
    }

    @Test
    @DisplayName("취소 미리보기 — 24시간 미만 → 100% 위약금")
    void cancelPreview_fullPenalty() {
        Booking booking = Booking.builder()
                .status(BookingStatus.CONFIRMED)
                .startAt(Instant.now().plus(Duration.ofHours(10)))
                .endAt(Instant.now().plus(Duration.ofHours(12)))
                .totalPrice(20000)
                .hours(2)
                .headcount(1)
                .build();
        CancelPreview preview = service.calculateCancelPreview(booking, Instant.now());
        assertThat(preview.refundAmount()).isZero();
        assertThat(preview.isCancellable()).isTrue();
    }

    @Test
    @DisplayName("결제 총금액 계산")
    void calculateTotalPrice() {
        assertThat(service.calculateTotalPrice(10000, 3)).isEqualTo(30000);
    }
}
