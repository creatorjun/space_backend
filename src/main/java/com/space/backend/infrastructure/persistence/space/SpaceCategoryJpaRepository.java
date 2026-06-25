package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.SpaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpaceCategoryJpaRepository extends JpaRepository<SpaceCategory, UUID> {
    List<SpaceCategory> findAllByIsActiveTrueOrderByDisplayOrderAsc();
}
