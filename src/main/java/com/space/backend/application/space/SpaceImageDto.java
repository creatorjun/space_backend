package com.space.backend.application.space;

import com.space.backend.domain.space.SpaceImage;

public record SpaceImageDto(String imageUrl, int displayOrder) {
    public static SpaceImageDto from(SpaceImage i) {
        return new SpaceImageDto(i.getImageUrl(), i.getDisplayOrder());
    }
}
