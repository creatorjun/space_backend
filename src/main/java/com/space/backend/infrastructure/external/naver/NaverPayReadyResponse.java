package com.space.backend.infrastructure.external.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverPayReadyResponse {
    private String code;
    private String message;
    private Body body;

    @Getter @NoArgsConstructor
    public static class Body {
        @JsonProperty("paymentId") private String paymentId;
        @JsonProperty("checkoutPage") private String checkoutPage;
    }
}
