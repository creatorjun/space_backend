package com.space.backend.infrastructure.external.kakao;

import com.space.backend.infrastructure.config.OAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthProperties {

    private final OAuthProperties oAuthProperties;

    public String getClientId() { return oAuthProperties.getKakao().getClientId(); }
    public String getClientSecret() { return oAuthProperties.getKakao().getClientSecret(); }
    public String getRedirectUri() { return oAuthProperties.getKakao().getRedirectUri(); }

    public static final String AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
    public static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    public static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
}
