package com.space.backend.application.auth;

import com.space.backend.domain.user.*;
import com.space.backend.infrastructure.external.kakao.KakaoOAuthClient;
import com.space.backend.infrastructure.external.kakao.KakaoUserInfoResponse;
import com.space.backend.infrastructure.external.naver.NaverOAuthClient;
import com.space.backend.infrastructure.external.naver.NaverUserInfoResponse;
import com.space.backend.infrastructure.persistence.user.UserSocialAccountJpaRepository;
import com.space.backend.infrastructure.security.AesEncryptionService;
import com.space.backend.infrastructure.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSocialAccountJpaRepository socialAccountRepository;
    private final NaverOAuthClient naverOAuthClient;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final AesEncryptionService aesEncryptionService;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public TokenResponse processOAuthCallback(OAuthProvider provider, String code, String state) {
        String rawSocialId;
        String name;
        String email;
        String profileImageUrl;

        if (provider == OAuthProvider.NAVER) {
            var tokenResp = naverOAuthClient.fetchToken(code, state);
            NaverUserInfoResponse.Response info = naverOAuthClient.fetchUserInfo(tokenResp.getAccessToken()).getResponse();
            rawSocialId = info.getId();
            name = info.getName();
            email = info.getEmail();
            profileImageUrl = info.getProfileImage();
        } else {
            var tokenResp = kakaoOAuthClient.fetchToken(code);
            KakaoUserInfoResponse info = kakaoOAuthClient.fetchUserInfo(tokenResp.getAccessToken());
            rawSocialId = String.valueOf(info.getId());
            name = info.getKakaoAccount() != null && info.getKakaoAccount().getProfile() != null
                    ? info.getKakaoAccount().getProfile().getNickname() : "";
            email = info.getKakaoAccount() != null ? info.getKakaoAccount().getEmail() : null;
            profileImageUrl = info.getKakaoAccount() != null && info.getKakaoAccount().getProfile() != null
                    ? info.getKakaoAccount().getProfile().getProfileImageUrl() : null;
        }

        String encryptedSocialId = aesEncryptionService.encrypt(rawSocialId);
        User user = userRepository.findBySocialAccount(provider, encryptedSocialId)
                .orElseGet(() -> createUser(provider, encryptedSocialId, name, email, profileImageUrl));

        return issueTokens(user);
    }

    @Override
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (stored.isExpired()) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        User user = stored.getUser();
        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        return new TokenResponse(newAccessToken, refreshToken, 0);
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void linkSocialAccount(UUID userId, OAuthProvider provider, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (socialAccountRepository.existsByUserIdAndProvider(userId, provider)) {
            return;
        }
        String rawSocialId;
        if (provider == OAuthProvider.NAVER) {
            var tokenResp = naverOAuthClient.fetchToken(code, null);
            rawSocialId = naverOAuthClient.fetchUserInfo(tokenResp.getAccessToken()).getResponse().getId();
        } else {
            var tokenResp = kakaoOAuthClient.fetchToken(code);
            rawSocialId = String.valueOf(kakaoOAuthClient.fetchUserInfo(tokenResp.getAccessToken()).getId());
        }
        String encrypted = aesEncryptionService.encrypt(rawSocialId);
        socialAccountRepository.save(UserSocialAccount.builder()
                .user(user)
                .provider(provider)
                .socialId(encrypted)
                .build());
    }

    private User createUser(OAuthProvider provider, String encryptedSocialId,
                            String name, String email, String profileImageUrl) {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .role(UserRole.USER)
                .isActive(true)
                .build();
        User saved = userRepository.save(newUser);
        socialAccountRepository.save(UserSocialAccount.builder()
                .user(saved)
                .provider(provider)
                .socialId(encryptedSocialId)
                .build());
        return saved;
    }

    private TokenResponse issueTokens(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshTokenStr = jwtProvider.generateRefreshToken(user.getId());
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(jwtProvider.getRefreshTokenExpiry())
                .build();
        refreshTokenRepository.save(refreshToken);
        return new TokenResponse(accessToken, refreshTokenStr, 0);
    }
}
