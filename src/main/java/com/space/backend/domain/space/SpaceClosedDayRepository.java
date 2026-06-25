// src/main/java/com/space/backend/domain/space/SpaceClosedDayRepository.java
package com.space.backend.domain.space;

import java.time.LocalDate;
import java.util.UUID;

public interface SpaceClosedDayRepository {
    SpaceClosedDay save(SpaceClosedDay closedDay);
    void deleteBySpaceIdAndClosedDate(UUID spaceId, LocalDate date);
}
