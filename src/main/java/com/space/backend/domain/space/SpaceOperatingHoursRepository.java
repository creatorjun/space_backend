// src/main/java/com/space/backend/domain/space/SpaceOperatingHoursRepository.java
package com.space.backend.domain.space;

import java.util.UUID;

public interface SpaceOperatingHoursRepository {
    SpaceOperatingHours save(SpaceOperatingHours hours);
    void deleteBySpaceId(UUID spaceId);
}
