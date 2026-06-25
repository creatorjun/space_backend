package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpaceRepositoryImpl implements SpaceRepository {

    private final SpaceJpaRepository jpa;

    @Override
    public Optional<Space> findById(UUID id) {
        return jpa.findByIdWithDetails(id);
    }

    @Override
    public List<Space> findActiveByCategoryId(UUID categoryId) {
        return jpa.findActiveByCategoryId(categoryId);
    }

    @Override
    public List<Space> findAll() {
        return jpa.findAll();
    }

    @Override
    public Space save(Space space) {
        return jpa.save(space);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
