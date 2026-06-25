package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.SpaceCategory;
import com.space.backend.domain.space.SpaceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpaceCategoryRepositoryImpl implements SpaceCategoryRepository {

    private final SpaceCategoryJpaRepository jpa;

    @Override
    public Optional<SpaceCategory> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<SpaceCategory> findAllActive() {
        return jpa.findAllByIsActiveTrueOrderByDisplayOrderAsc();
    }

    @Override
    public List<SpaceCategory> findAll() {
        return jpa.findAll();
    }

    @Override
    public SpaceCategory save(SpaceCategory category) {
        return jpa.save(category);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
