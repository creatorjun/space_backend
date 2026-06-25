// src/main/java/com/space/backend/application/payment/PaymentGatewayReadyCommand.java
package com.space.backend.application.payment;

public record PaymentGatewayReadyCommand(
        String pgOrderId,
        String userId,
        String itemName,
        int amount
) {}
