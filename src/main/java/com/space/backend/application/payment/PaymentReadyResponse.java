package com.space.backend.application.payment;

public record PaymentReadyResponse(
        String pgOrderId,
        String redirectUrl,
        String provider
) {}
