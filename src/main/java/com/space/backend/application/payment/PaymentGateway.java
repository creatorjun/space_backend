// src/main/java/com/space/backend/application/payment/PaymentGateway.java
package com.space.backend.application.payment;

public interface PaymentGateway {
    PaymentGatewayReadyResult ready(PaymentGatewayReadyCommand command);
    PaymentGatewayApproveResult approve(PaymentGatewayApproveCommand command);
    void refund(PaymentGatewayRefundCommand command);
}
