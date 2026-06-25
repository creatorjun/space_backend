// src/main/java/com/space/backend/application/payment/PaymentGatewayReadyResult.java
package com.space.backend.application.payment;

public record PaymentGatewayReadyResult(
        String redirectUrl,
        String tid
) {}
