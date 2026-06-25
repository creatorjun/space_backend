// src/main/java/com/space/backend/domain/exception/UnauthorizedAccessException.java
package com.space.backend.domain.exception;

public class UnauthorizedAccessException extends DomainException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public static UnauthorizedAccessException noPermission() {
        return new UnauthorizedAccessException("접근 권한이 없습니다");
    }
}
