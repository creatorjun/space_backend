package com.space.backend.infrastructure.persistence.user;

import com.space.backend.domain.user.OAuthProvider;
import com.space.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT u FROM User u
            JOIN UserSocialAccount sa ON sa.user = u
            WHERE sa.provider = :provider AND sa.socialId = :socialId
            """)
    Optional<User> findBySocialAccount(@Param("provider") OAuthProvider provider,
                                       @Param("socialId") String socialId);
}
