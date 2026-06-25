package com.space.backend.infrastructure.persistence.space;

import com.space.backend.domain.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<Space, UUID> {

    @Query("""
            SELECT s FROM Space s
            LEFT JOIN FETCH s.images
            LEFT JOIN FETCH s.operatingHours
            WHERE s.category.id = :categoryId AND s.isActive = true
            ORDER BY s.displayOrder ASC
            """)
    List<Space> findActiveByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("""
            SELECT DISTINCT s FROM Space s
            LEFT JOIN FETCH s.images
            LEFT JOIN FETCH s.operatingHours
            LEFT JOIN FETCH s.closedDays
            WHERE s.id = :id
            """)
    java.util.Optional<Space> findByIdWithDetails(@Param("id") UUID id);
}
