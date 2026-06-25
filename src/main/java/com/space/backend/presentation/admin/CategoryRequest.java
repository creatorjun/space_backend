package com.space.backend.presentation.admin;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String name,
        int displayOrder
) {}
