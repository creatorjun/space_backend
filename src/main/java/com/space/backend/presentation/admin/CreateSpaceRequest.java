package com.space.backend.presentation.admin;

import com.space.backend.application.admin.CreateSpaceCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateSpaceRequest(
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
        List<String> imageUrls
) {
    public CreateSpaceCommand toCommand() {
        return new CreateSpaceCommand(categoryId, name, description, address,
                capacity, minHours, maxHours, pricePerHour, thumbnailUrl, displayOrder, imageUrls);
    }
}
