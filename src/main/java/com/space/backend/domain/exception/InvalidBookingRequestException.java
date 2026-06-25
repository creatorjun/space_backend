// src/main/java/com/space/backend/domain/exception/InvalidBookingRequestException.java
package com.space.backend.domain.exception;

public class InvalidBookingRequestException extends DomainException {
    public InvalidBookingRequestException(String message) {
        super(message);
    }
}
