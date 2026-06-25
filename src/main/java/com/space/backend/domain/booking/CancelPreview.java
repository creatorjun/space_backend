package com.space.backend.domain.booking;

public record CancelPreview(
        int originalAmount,
        int refundAmount,
        int penaltyAmount,
        String penaltyReason,
        boolean isCancellable
) {}
