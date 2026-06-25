package com.space.backend.presentation.user;

import com.space.backend.application.auth.AuthService;
import com.space.backend.domain.user.OAuthProvider;
import com.space.backend.domain.user.User;
import com.space.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping("/link/naver")
    public ResponseEntity<Void> linkNaver(
            @AuthenticationPrincipal String userIdStr,
            @RequestParam String code) {
        authService.linkSocialAccount(UUID.fromString(userIdStr), OAuthProvider.NAVER, code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/link/kakao")
    public ResponseEntity<Void> linkKakao(
            @AuthenticationPrincipal String userIdStr,
            @RequestParam String code) {
        authService.linkSocialAccount(UUID.fromString(userIdStr), OAuthProvider.KAKAO, code);
        return ResponseEntity.noContent().build();
    }
}
