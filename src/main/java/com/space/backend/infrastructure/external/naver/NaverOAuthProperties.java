package com.space.backend.infrastructure.external.naver;

import com.space.backend.infrastructure.config.OAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverOAuthProperties {

    private final OAuthProperties oAuthProperties;

    public String getClientId() { return oAuthProperties.getNaver().getClientId(); }
    public String getClientSecret() { return oAuthProperties.getNaver().getClientSecret(); }
    public String getRedirectUri() { return oAuthProperties.getNaver().getRedirectUri(); }

    public static final String AUTH_URL = "https://nid.naver.com/oauth2.0/authorize";
    public static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    public static final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";
}
