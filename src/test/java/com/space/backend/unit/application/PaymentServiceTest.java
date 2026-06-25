package com.space.backend.unit.application;

import com.space.backend.application.payment.PaymentServiceImpl;
import com.space.backend.domain.booking.*;
import com.space.backend.domain.payment.*;
import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceCategory;
import com.space.backend.domain.user.User;
import com.space.backend.domain.user.UserRepository;
import com.space.backend.domain.user.UserRole;
import com.space.backend.infrastructure.external.kakao.*;
import com.space.backend.infrastructure.external.naver.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock PaymentRepository paymentRepository;
    @Mock BookingRepository bookingRepository;
    @Mock UserRepository userRepository;
    @Mock NaverPayClient naverPayClient;
    @Mock KakaoPayClient kakaoPayClient;

    @InjectMocks PaymentServiceImpl paymentService;

    private UUID userId;
    private UUID bookingId;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        userId    = UUID.randomUUID();
        bookingId = UUID.randomUUID();

        user = User.builder()
                .name("TestUser").role(UserRole.USER).isActive(true).build();

        SpaceCategory cat = SpaceCategory.builder().name("cat").build();
        Space space = Space.builder()
                .category(cat).name("Test Space")
                .capacity(5).minHours(1).maxHours(4)
                .pricePerHour(20000).isActive(true).build();

        booking = Booking.builder()
                .user(user).space(space)
                .startAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .endAt(Instant.now().plus(3, ChronoUnit.HOURS).plus(1, ChronoUnit.DAYS))
                .hours(3).headcount(2).totalPrice(60000)
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("PENDING 상태가 아단 예약 → ready 시 IllegalStateException")
    void readyNaverPay_notPending() {
        booking.confirm();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.readyNaverPay(userId, bookingId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("다른 사용자 예약 → ready 시 SecurityException")
    void readyNaverPay_forbidden() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> paymentService.readyNaverPay(UUID.randomUUID(), bookingId))
                .isInstanceOf(SecurityException.class);
    }
}
