package com.space.backend.presentation.admin;

import com.space.backend.application.admin.UpdateSpaceCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateSpaceRequest(
        @NotNull UUID categoryId,
        @NotBlank String name,
        String description,
        String address,
        @Min(1) int capacity,
        @Min(1) int minHours,
        @Min(1) int maxHours,
        @Min(0) int pricePerHour,
        String thumbnailUrl,
        int displayOrder,
        boolean isActive,
        List<String> imageUrls
) {
    public UpdateSpaceCommand toCommand() {
        return new UpdateSpaceCommand(categoryId, name, description, address,
                capacity, minHours, maxHours, pricePerHour, thumbnailUrl,
                displayOrder, isActive, imageUrls);
    }
}
