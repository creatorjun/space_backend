package com.space.backend.application.space;

import com.space.backend.domain.space.Space;

import java.util.UUID;

public record SpaceSummaryDto(
        UUID id,
        String name,
        String thumbnailUrl,
        int pricePerHour,
        int capacity,
        int displayOrder
) {
    public static SpaceSummaryDto from(Space s) {
        return new SpaceSummaryDto(
                s.getId(), s.getName(), s.getThumbnailUrl(),
                s.getPricePerHour(), s.getCapacity(), s.getDisplayOrder());
    }
}
