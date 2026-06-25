package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.SpaceClosedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface SpaceClosedDayJpaRepository extends JpaRepository<SpaceClosedDay, UUID> {
    @Modifying
    @Query("DELETE FROM SpaceClosedDay d WHERE d.space.id = :spaceId AND d.closedDate = :date")
    void deleteBySpaceIdAndClosedDate(@Param("spaceId") UUID spaceId, @Param("date") LocalDate date);
}
