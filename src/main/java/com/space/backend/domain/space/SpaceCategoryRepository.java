package com.space.backend.domain.space;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpaceCategoryRepository {
    Optional<SpaceCategory> findById(UUID id);
    List<SpaceCategory> findAllActive();
    List<SpaceCategory> findAll();
    SpaceCategory save(SpaceCategory category);
    void deleteById(UUID id);
}
