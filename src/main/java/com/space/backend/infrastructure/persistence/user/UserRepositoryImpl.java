package com.space.backend.infrastructure.persistence.user;

import com.space.backend.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findBySocialAccount(OAuthProvider provider, String encryptedSocialId) {
        return userJpaRepository.findBySocialAccount(provider, encryptedSocialId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
