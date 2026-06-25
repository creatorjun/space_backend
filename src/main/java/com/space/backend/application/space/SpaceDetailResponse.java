package com.space.backend.application.space;

import com.space.backend.domain.space.Space;

import java.util.List;
import java.util.UUID;

public record SpaceDetailResponse(
        UUID id,
        String name,
        String description,
        String address,
        int capacity,
        int minHours,
        int maxHours,
        int pricePerHour,
        String thumbnailUrl,
        CategoryDto category,
        List<SpaceImageDto> images,
        List<OperatingHoursDto> operatingHours
) {
    public static SpaceDetailResponse from(Space s) {
        return new SpaceDetailResponse(
                s.getId(), s.getName(), s.getDescription(), s.getAddress(),
                s.getCapacity(), s.getMinHours(), s.getMaxHours(), s.getPricePerHour(),
                s.getThumbnailUrl(),
                CategoryDto.from(s.getCategory()),
                s.getImages().stream().map(SpaceImageDto::from).toList(),
                s.getOperatingHours().stream().map(OperatingHoursDto::from).toList()
        );
    }
}
