package com.space.backend.unit.application;

import com.space.backend.application.booking.*;
import com.space.backend.domain.booking.*;
import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceCategory;
import com.space.backend.domain.user.User;
import com.space.backend.domain.user.UserRepository;
import com.space.backend.domain.user.UserRole;
import com.space.backend.domain.space.SpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock SpaceRepository spaceRepository;
    @Mock UserRepository userRepository;
    @Mock BookingDomainService bookingDomainService;

    @InjectMocks BookingServiceImpl bookingService;

    private UUID userId;
    private UUID spaceId;
    private User user;
    private Space space;

    @BeforeEach
    void setUp() {
        userId  = UUID.randomUUID();
        spaceId = UUID.randomUUID();

        user = User.builder()
                .name("TestUser")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        SpaceCategory cat = SpaceCategory.builder().name("cat").build();
        space = Space.builder()
                .category(cat)
                .name("Test Space")
                .capacity(10)
                .minHours(1)
                .maxHours(8)
                .pricePerHour(10000)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("예약 생성 성공 — PENDING + pendingExpiresAt 설정")
    void createBooking_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(bookingDomainService.calculateTotalPrice(10000, 2)).thenReturn(20000);
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateBookingCommand cmd = new CreateBookingCommand(
                spaceId,
                Instant.now().plus(1, ChronoUnit.DAYS),
                2, 3, PaymentType.KAKAO_PAY, null
        );

        BookingResponse resp = bookingService.createBooking(userId, cmd);
        assertThat(resp.booking().status()).isEqualTo(BookingStatus.PENDING);
        assertThat(resp.booking().totalPrice()).isEqualTo(20000);
        assertThat(resp.booking().pendingExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("다른 사용자의 예약 조회 → SecurityException")
    void getBookingById_forbidden() {
        UUID otherId = UUID.randomUUID();
        Booking booking = Booking.builder()
                .user(User.builder().name("other").role(UserRole.USER).isActive(true).build())
                .space(space)
                .startAt(Instant.now())
                .endAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .hours(1).headcount(1).totalPrice(10000)
                .status(BookingStatus.PENDING)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(userId, UUID.randomUUID()))
                .isInstanceOf(SecurityException.class);
    }
}
