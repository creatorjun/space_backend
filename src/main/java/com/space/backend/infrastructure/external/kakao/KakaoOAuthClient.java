package com.space.backend.infrastructure.external.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final RestClient restClient;
    private final KakaoOAuthProperties properties;

    public KakaoTokenResponse fetchToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("redirect_uri", properties.getRedirectUri());
        params.add("code", code);

        return restClient.post()
                .uri(KakaoOAuthProperties.TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri(KakaoOAuthProperties.USER_INFO_URL)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }

    public String buildAuthorizationUrl() {
        return KakaoOAuthProperties.AUTH_URL
                + "?response_type=code"
                + "&client_id=" + properties.getClientId()
                + "&redirect_uri=" + properties.getRedirectUri();
    }
}
