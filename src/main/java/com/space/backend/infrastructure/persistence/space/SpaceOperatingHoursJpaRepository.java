package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.SpaceOperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpaceOperatingHoursJpaRepository extends JpaRepository<SpaceOperatingHours, UUID> {
    @Modifying
    @Query("DELETE FROM SpaceOperatingHours h WHERE h.space.id = :spaceId")
    void deleteBySpaceId(@Param("spaceId") UUID spaceId);
}
