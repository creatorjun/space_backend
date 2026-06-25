package com.space.backend.infrastructure.external.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoPayReadyResponse {
    @JsonProperty("tid")            private String tid;
    @JsonProperty("next_redirect_app_url")     private String nextRedirectAppUrl;
    @JsonProperty("next_redirect_mobile_url")  private String nextRedirectMobileUrl;
    @JsonProperty("next_redirect_pc_url")      private String nextRedirectPcUrl;
    @JsonProperty("created_at")     private String createdAt;
}
