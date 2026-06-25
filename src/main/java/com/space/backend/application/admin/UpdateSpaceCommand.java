package com.space.backend.application.admin;

import java.util.List;
import java.util.UUID;

public record UpdateSpaceCommand(
        UUID categoryId,
        String name,
        String description,
        String address,
        int capacity,
        int minHours,
        int maxHours,
        int pricePerHour,
        String thumbnailUrl,
        int displayOrder,
        boolean isActive,
        List<String> imageUrls
) {}
