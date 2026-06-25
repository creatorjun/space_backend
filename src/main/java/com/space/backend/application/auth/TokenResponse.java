package com.space.backend.application.auth;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long accessExpiresIn
) {}
