// src/main/java/com/space/backend/domain/exception/EntityNotFoundException.java
package com.space.backend.domain.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException booking(Object id) {
        return new EntityNotFoundException("Booking not found: " + id);
    }

    public static EntityNotFoundException payment(Object id) {
        return new EntityNotFoundException("Payment not found: " + id);
    }

    public static EntityNotFoundException space(Object id) {
        return new EntityNotFoundException("Space not found: " + id);
    }

    public static EntityNotFoundException category(Object id) {
        return new EntityNotFoundException("Category not found: " + id);
    }

    public static EntityNotFoundException user(Object id) {
        return new EntityNotFoundException("User not found: " + id);
    }
}
