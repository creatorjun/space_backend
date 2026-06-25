package com.space.backend.domain.space;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpaceRepository {
    Optional<Space> findById(UUID id);
    List<Space> findActiveByCategoryId(UUID categoryId);
    List<Space> findAll();
    Space save(Space space);
    void deleteById(UUID id);
}
