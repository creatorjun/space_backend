package com.space.backend.domain.booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
    List<Booking> findByUserId(UUID userId);
    List<Booking> findBySpaceAndDateRange(UUID spaceId, Instant from, Instant to);
    List<Booking> findBySpaceAndMonth(UUID spaceId, Instant monthStart, Instant monthEnd);
    List<Booking> findExpiredPending(Instant now);
}
