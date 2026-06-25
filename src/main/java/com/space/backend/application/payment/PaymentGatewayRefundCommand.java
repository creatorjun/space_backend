// src/main/java/com/space/backend/application/payment/PaymentGatewayRefundCommand.java
package com.space.backend.application.payment;

public record PaymentGatewayRefundCommand(
        String pgTransactionId,
        int cancelAmount,
        String reason
) {}
