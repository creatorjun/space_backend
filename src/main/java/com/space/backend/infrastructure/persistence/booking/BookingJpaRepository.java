package com.space.backend.infrastructure.persistence.booking;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.space.id = :spaceId
              AND b.status IN :statuses
              AND b.startAt < :to
              AND b.endAt > :from
            """)
    List<Booking> findBySpaceAndDateRange(
            @Param("spaceId") UUID spaceId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("statuses") List<BookingStatus> statuses
    );

    @Query("""
            SELECT b FROM Booking b
            WHERE b.space.id = :spaceId
              AND b.status IN :statuses
              AND b.startAt >= :monthStart
              AND b.startAt < :monthEnd
            """)
    List<Booking> findBySpaceAndMonth(
            @Param("spaceId") UUID spaceId,
            @Param("monthStart") Instant monthStart,
            @Param("monthEnd") Instant monthEnd,
            @Param("statuses") List<BookingStatus> statuses
    );

    @Query("""
            SELECT b FROM Booking b
            WHERE b.status = 'PENDING'
              AND b.pendingExpiresAt IS NOT NULL
              AND b.pendingExpiresAt < :now
            """)
    List<Booking> findExpiredPending(@Param("now") Instant now);

    @Modifying
    @Query("""
            UPDATE Booking b
            SET b.status = 'EXPIRED', b.updatedAt = :now
            WHERE b.status = 'PENDING'
              AND b.pendingExpiresAt IS NOT NULL
              AND b.pendingExpiresAt < :now
            """)
    int bulkExpire(@Param("now") Instant now);
}
