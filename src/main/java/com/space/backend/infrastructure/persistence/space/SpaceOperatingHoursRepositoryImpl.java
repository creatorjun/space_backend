// src/main/java/com/space/backend/infrastructure/persistence/space/SpaceOperatingHoursRepositoryImpl.java
package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.SpaceOperatingHours;
import com.space.backend.domain.space.SpaceOperatingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpaceOperatingHoursRepositoryImpl implements SpaceOperatingHoursRepository {

    private final SpaceOperatingHoursJpaRepository jpaRepository;

    @Override
    public SpaceOperatingHours save(SpaceOperatingHours hours) {
        return jpaRepository.save(hours);
    }

    @Override
    public void deleteBySpaceId(UUID spaceId) {
        jpaRepository.deleteBySpaceId(spaceId);
    }
}
