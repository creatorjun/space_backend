package com.space.backend.infrastructure.persistence.booking;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingRepository;
import com.space.backend.domain.booking.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaRepository jpa;

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private static final List<BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    @Override
    public Booking save(Booking booking) {
        return jpa.save(booking);
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<Booking> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Booking> findBySpaceAndDateRange(UUID spaceId, Instant from, Instant to) {
        return jpa.findBySpaceAndDateRange(spaceId, from, to, ACTIVE_STATUSES);
    }

    @Override
    public List<Booking> findBySpaceAndMonth(UUID spaceId, Instant monthStart, Instant monthEnd) {
        return jpa.findBySpaceAndMonth(spaceId, monthStart, monthEnd, ACTIVE_STATUSES);
    }

    @Override
    public List<Booking> findExpiredPending(Instant now) {
        return jpa.findExpiredPending(now);
    }

    @Override
    public int bulkExpire(Instant now) {
        return jpa.bulkExpire(now);
    }
}
