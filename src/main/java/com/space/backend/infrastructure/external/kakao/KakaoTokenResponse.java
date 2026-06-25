package com.space.backend.infrastructure.external.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenResponse {
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("refresh_token") private String refreshToken;
    @JsonProperty("token_type") private String tokenType;
    @JsonProperty("expires_in") private int expiresIn;
    private String error;
    @JsonProperty("error_description") private String errorDescription;
}
