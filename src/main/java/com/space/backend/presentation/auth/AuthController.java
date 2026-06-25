package com.space.backend.presentation.auth;

import com.space.backend.application.auth.AuthService;
import com.space.backend.application.auth.TokenResponse;
import com.space.backend.domain.user.OAuthProvider;
import com.space.backend.infrastructure.external.kakao.KakaoOAuthClient;
import com.space.backend.infrastructure.external.naver.NaverOAuthClient;
import com.space.backend.infrastructure.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final NaverOAuthClient naverOAuthClient;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final JwtProvider jwtProvider;

    @GetMapping("/naver")
    public ResponseEntity<Void> naverLogin() {
        String state = UUID.randomUUID().toString();
        String url = naverOAuthClient.buildAuthorizationUrl(state);
        return ResponseEntity.status(302)
                .header("Location", url)
                .build();
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<TokenResponse> naverCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {
        TokenResponse token = authService.processOAuthCallback(OAuthProvider.NAVER, code, state);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/kakao")
    public ResponseEntity<Void> kakaoLogin() {
        String url = kakaoOAuthClient.buildAuthorizationUrl();
        return ResponseEntity.status(302)
                .header("Location", url)
                .build();
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam String code) {
        TokenResponse token = authService.processOAuthCallback(OAuthProvider.KAKAO, code, null);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        TokenResponse token = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal String userIdStr) {
        authService.logout(UUID.fromString(userIdStr));
        return ResponseEntity.noContent().build();
    }
}
