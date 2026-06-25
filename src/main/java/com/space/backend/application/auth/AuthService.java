package com.space.backend.application.auth;

import com.space.backend.domain.user.OAuthProvider;

import java.util.UUID;

public interface AuthService {
    TokenResponse processOAuthCallback(OAuthProvider provider, String code, String state);
    TokenResponse refreshAccessToken(String refreshToken);
    void logout(UUID userId);
    void linkSocialAccount(UUID userId, OAuthProvider provider, String code);
}
