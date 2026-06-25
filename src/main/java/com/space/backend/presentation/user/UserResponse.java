package com.space.backend.presentation.user;

import com.space.backend.domain.user.User;
import com.space.backend.domain.user.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String profileImageUrl,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfileImageUrl(),
                user.getRole()
        );
    }
}
