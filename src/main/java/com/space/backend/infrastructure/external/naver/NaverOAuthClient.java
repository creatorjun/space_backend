package com.space.backend.infrastructure.external.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class NaverOAuthClient {

    private final RestClient restClient;
    private final NaverOAuthProperties properties;

    public NaverTokenResponse fetchToken(String code, String state) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("redirect_uri", properties.getRedirectUri());
        params.add("code", code);
        params.add("state", state);

        return restClient.post()
                .uri(NaverOAuthProperties.TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(NaverTokenResponse.class);
    }

    public NaverUserInfoResponse fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri(NaverOAuthProperties.USER_INFO_URL)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(NaverUserInfoResponse.class);
    }

    public String buildAuthorizationUrl(String state) {
        return NaverOAuthProperties.AUTH_URL
                + "?response_type=code"
                + "&client_id=" + properties.getClientId()
                + "&redirect_uri=" + properties.getRedirectUri()
                + "&state=" + state;
    }
}
