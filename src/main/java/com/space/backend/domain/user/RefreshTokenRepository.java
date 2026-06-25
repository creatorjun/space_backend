package com.space.backend.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    RefreshToken save(RefreshToken token);
}
