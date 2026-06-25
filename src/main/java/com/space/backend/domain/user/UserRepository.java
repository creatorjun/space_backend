package com.space.backend.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findBySocialAccount(OAuthProvider provider, String encryptedSocialId);
    User save(User user);
}
