package com.space.backend.infrastructure.persistence.user;

import com.space.backend.domain.user.OAuthProvider;
import com.space.backend.domain.user.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSocialAccountJpaRepository extends JpaRepository<UserSocialAccount, UUID> {
    Optional<UserSocialAccount> findByProviderAndSocialId(OAuthProvider provider, String socialId);
    boolean existsByUserIdAndProvider(UUID userId, OAuthProvider provider);
}
