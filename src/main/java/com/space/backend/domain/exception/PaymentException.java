// src/main/java/com/space/backend/domain/exception/PaymentException.java
package com.space.backend.domain.exception;

public class PaymentException extends DomainException {
    public PaymentException(String message) {
        super(message);
    }

    public static PaymentException notPayable() {
        return new PaymentException("결제 가능한 상태가 아닙니다");
    }

    public static PaymentException notFound(String pgOrderId) {
        return new PaymentException("Payment not found: " + pgOrderId);
    }
}
