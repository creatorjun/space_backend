// src/main/java/com/space/backend/application/booking/BookingServiceImpl.java
package com.space.backend.application.booking;

import com.space.backend.domain.booking.*;
import com.space.backend.domain.exception.*;
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
                .orElseThrow(() -> EntityNotFoundException.user(userId));
        Space space = spaceRepository.findById(cmd.spaceId())
                .orElseThrow(() -> EntityNotFoundException.space(cmd.spaceId()));

        if (!space.isActive()) {
            throw new InvalidBookingRequestException("예약 불가능한 공간입니다");
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
                .orElseThrow(() -> EntityNotFoundException.booking(bookingId));
        if (!booking.getUser().getId().equals(userId)) {
            throw UnauthorizedAccessException.noPermission();
        }
        return BookingResult.from(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public CancelPreview getCancelPreview(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> EntityNotFoundException.booking(bookingId));
        if (!booking.getUser().getId().equals(userId)) {
            throw UnauthorizedAccessException.noPermission();
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidStatusException("취소 가능한 상태가 아닙니다");
        }
        return bookingDomainService.calculateCancelPreview(booking, Instant.now());
    }

    @Override
    @Transactional
    public void requestCancel(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> EntityNotFoundException.booking(bookingId));
        if (!booking.getUser().getId().equals(userId)) {
            throw UnauthorizedAccessException.noPermission();
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidStatusException("취소 신청 가능한 상태가 아닙니다");
        }
        CancelPreview preview = bookingDomainService.calculateCancelPreview(booking, Instant.now());
        if (!preview.isCancellable()) {
            throw new InvalidStatusException(preview.penaltyReason());
        }
        booking.requestCancel();
        bookingRepository.save(booking);
    }
}
