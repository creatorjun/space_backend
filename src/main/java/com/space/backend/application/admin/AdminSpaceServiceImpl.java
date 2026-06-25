// src/main/java/com/space/backend/application/admin/AdminSpaceServiceImpl.java
package com.space.backend.application.admin;

import com.space.backend.application.space.CategoryDto;
import com.space.backend.application.space.SpaceDetailResponse;
import com.space.backend.domain.exception.EntityNotFoundException;
import com.space.backend.domain.space.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminSpaceServiceImpl implements AdminSpaceService {

    private final SpaceCategoryRepository categoryRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceOperatingHoursRepository hoursRepository;
    private final SpaceClosedDayRepository closedDayRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryCommand cmd) {
        SpaceCategory cat = SpaceCategory.builder()
                .name(cmd.name())
                .displayOrder(cmd.displayOrder())
                .build();
        return CategoryDto.from(categoryRepository.save(cat));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(UUID categoryId, CreateCategoryCommand cmd) {
        SpaceCategory cat = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityNotFoundException.category(categoryId));
        cat.update(cmd.name(), cmd.displayOrder());
        return CategoryDto.from(categoryRepository.save(cat));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public SpaceDetailResponse createSpace(CreateSpaceCommand cmd) {
        SpaceCategory category = categoryRepository.findById(cmd.categoryId())
                .orElseThrow(() -> EntityNotFoundException.category(cmd.categoryId()));

        Space space = Space.builder()
                .category(category)
                .name(cmd.name())
                .description(cmd.description())
                .address(cmd.address())
                .capacity(cmd.capacity())
                .minHours(cmd.minHours())
                .maxHours(cmd.maxHours())
                .pricePerHour(cmd.pricePerHour())
                .thumbnailUrl(cmd.thumbnailUrl())
                .displayOrder(cmd.displayOrder())
                .build();

        if (cmd.imageUrls() != null) {
            for (int i = 0; i < cmd.imageUrls().size(); i++) {
                space.getImages().add(SpaceImage.builder()
                        .space(space)
                        .imageUrl(cmd.imageUrls().get(i))
                        .displayOrder(i)
                        .build());
            }
        }
        return SpaceDetailResponse.from(spaceRepository.save(space));
    }

    @Override
    @Transactional
    public SpaceDetailResponse updateSpace(UUID spaceId, UpdateSpaceCommand cmd) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> EntityNotFoundException.space(spaceId));
        SpaceCategory category = categoryRepository.findById(cmd.categoryId())
                .orElseThrow(() -> EntityNotFoundException.category(cmd.categoryId()));

        space.update(
                category, cmd.name(), cmd.description(), cmd.address(),
                cmd.capacity(), cmd.minHours(), cmd.maxHours(),
                cmd.pricePerHour(), cmd.thumbnailUrl(), cmd.displayOrder(), cmd.isActive()
        );

        space.getImages().clear();
        if (cmd.imageUrls() != null) {
            for (int i = 0; i < cmd.imageUrls().size(); i++) {
                space.getImages().add(SpaceImage.builder()
                        .space(space)
                        .imageUrl(cmd.imageUrls().get(i))
                        .displayOrder(i)
                        .build());
            }
        }
        return SpaceDetailResponse.from(spaceRepository.save(space));
    }

    @Override
    @Transactional
    public void deleteSpace(UUID spaceId) {
        spaceRepository.deleteById(spaceId);
    }

    @Override
    @Transactional
    public void updateOperatingHours(UUID spaceId, List<OperatingHoursCommand> commands) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> EntityNotFoundException.space(spaceId));
        hoursRepository.deleteBySpaceId(spaceId);
        for (OperatingHoursCommand cmd : commands) {
            hoursRepository.save(SpaceOperatingHours.builder()
                    .space(space)
                    .dayOfWeek(cmd.dayOfWeek())
                    .openTime(cmd.openTime())
                    .closeTime(cmd.closeTime())
                    .isClosed(cmd.isClosed())
                    .build());
        }
    }

    @Override
    @Transactional
    public void addClosedDay(UUID spaceId, AddClosedDayCommand cmd) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> EntityNotFoundException.space(spaceId));
        closedDayRepository.save(SpaceClosedDay.builder()
                .space(space)
                .closedDate(cmd.closedDate())
                .reason(cmd.reason())
                .build());
    }

    @Override
    @Transactional
    public void removeClosedDay(UUID spaceId, LocalDate date) {
        closedDayRepository.deleteBySpaceIdAndClosedDate(spaceId, date);
    }
}
