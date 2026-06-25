// src/main/java/com/space/backend/infrastructure/persistence/space/SpaceClosedDayRepositoryImpl.java
package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.SpaceClosedDay;
import com.space.backend.domain.space.SpaceClosedDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpaceClosedDayRepositoryImpl implements SpaceClosedDayRepository {

    private final SpaceClosedDayJpaRepository jpaRepository;

    @Override
    public SpaceClosedDay save(SpaceClosedDay closedDay) {
        return jpaRepository.save(closedDay);
    }

    @Override
    public void deleteBySpaceIdAndClosedDate(UUID spaceId, LocalDate date) {
        jpaRepository.deleteBySpaceIdAndClosedDate(spaceId, date);
    }
}
