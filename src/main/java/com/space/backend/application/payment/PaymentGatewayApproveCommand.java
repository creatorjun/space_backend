// src/main/java/com/space/backend/application/payment/PaymentGatewayApproveCommand.java
package com.space.backend.application.payment;

public record PaymentGatewayApproveCommand(
        String pgOrderId,
        String tid,
        String userId,
        String pgToken
) {}
