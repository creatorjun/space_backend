package com.space.backend.application.space;

import java.time.LocalDate;
import java.util.UUID;

public interface SpaceService {
    CategoriesResponse getCategories();
    SpaceListResponse getSpaces(UUID categoryId);
    SpaceDetailResponse getSpaceById(UUID id);
    AvailableDatesResponse getAvailableDates(UUID spaceId, int year, int month);
    AvailableSlotsResponse getAvailableSlots(UUID spaceId, LocalDate date);
}
