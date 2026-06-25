package com.space.backend.application.space;

import com.space.backend.domain.space.SpaceCategory;

import java.util.UUID;

public record CategoryDto(
        UUID id,
        String name,
        int displayOrder
) {
    public static CategoryDto from(SpaceCategory c) {
        return new CategoryDto(c.getId(), c.getName(), c.getDisplayOrder());
    }
}
