package com.space.backend.presentation.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RefundRequest(
        @Min(0) int refundAmountKrw,
        @NotBlank String reason
) {}
