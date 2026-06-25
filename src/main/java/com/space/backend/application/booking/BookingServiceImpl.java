// src/main/java/com/space/backend/application/booking/BookingServiceImpl.java
package com.space.backend.application.booking;

import com.space.backend.domain.booking.*;
import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceRepository;
import com.space.backend.domain.user.User;
import com.space.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final BookingDomainService bookingDomainService;

    @Value("${booking.pending-expiry-minutes:15}")
    private int pendingExpiryMinutes;

    @Override
    @Transactional
    public BookingResult createBooking(UUID userId, CreateBookingCommand cmd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Space space = spaceRepository.findById(cmd.spaceId())
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));

        if (!space.isActive()) {
            throw new IllegalArgumentException("예약 불가능한 공간입니다");
        }

        bookingDomainService.validateBookingRequest(space, cmd.startAt(), cmd.hours(), cmd.headcount());

        Instant endAt = cmd.startAt().plus(cmd.hours(), ChronoUnit.HOURS);
        int totalPrice = bookingDomainService.calculateTotalPrice(space.getPricePerHour(), cmd.hours());
        Instant pendingExpiresAt = Instant.now().plus(pendingExpiryMinutes, ChronoUnit.MINUTES);

        Booking booking = Booking.builder()
                .user(user)
                .space(space)
                .startAt(cmd.startAt())
                .endAt(endAt)
                .hours(cmd.hours())
                .headcount(cmd.headcount())
                .totalPrice(totalPrice)
                .paymentType(cmd.paymentType())
                .status(BookingStatus.PENDING)
                .pendingExpiresAt(pendingExpiresAt)
                .memo(cmd.memo())
                .build();

        return BookingResult.from(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingListResult getMyBookings(UUID userId) {
        List<BookingResult> results = bookingRepository.findByUserId(userId)
                .stream().map(BookingResult::from).toList();
        return new BookingListResult(results);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResult getBookingById(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new SecurityException("접근 권한이 없습니다");
        }
        return BookingResult.from(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public CancelPreview getCancelPreview(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new SecurityException("접근 권한이 없습니다");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("취소 가능한 상태가 아닙니다");
        }
        return bookingDomainService.calculateCancelPreview(booking, Instant.now());
    }

    @Override
    @Transactional
    public void requestCancel(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new SecurityException("접근 권한이 없습니다");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("취소 신청 가능한 상태가 아닙니다");
        }
        CancelPreview preview = bookingDomainService.calculateCancelPreview(booking, Instant.now());
        if (!preview.isCancellable()) {
            throw new IllegalArgumentException(preview.penaltyReason());
        }
        booking.requestCancel();
        bookingRepository.save(booking);
    }
}
