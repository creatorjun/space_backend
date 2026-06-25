// src/main/java/com/space/backend/domain/exception/DomainException.java
package com.space.backend.domain.exception;

public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
