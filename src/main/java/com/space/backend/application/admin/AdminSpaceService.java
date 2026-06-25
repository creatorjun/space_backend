package com.space.backend.application.admin;

import com.space.backend.application.space.SpaceDetailResponse;
import com.space.backend.application.space.CategoryDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AdminSpaceService {
    CategoryDto createCategory(CreateCategoryCommand command);
    CategoryDto updateCategory(UUID categoryId, CreateCategoryCommand command);
    void deleteCategory(UUID categoryId);

    SpaceDetailResponse createSpace(CreateSpaceCommand command);
    SpaceDetailResponse updateSpace(UUID spaceId, UpdateSpaceCommand command);
    void deleteSpace(UUID spaceId);

    void updateOperatingHours(UUID spaceId, List<OperatingHoursCommand> commands);
    void addClosedDay(UUID spaceId, AddClosedDayCommand command);
    void removeClosedDay(UUID spaceId, LocalDate date);
}
