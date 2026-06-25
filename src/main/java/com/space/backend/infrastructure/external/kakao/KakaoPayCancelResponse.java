package com.space.backend.infrastructure.external.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoPayCancelResponse {
    private String tid;
    @JsonProperty("status") private String status;
    @JsonProperty("canceled_amount") private CanceledAmount canceledAmount;

    @Getter @NoArgsConstructor
    public static class CanceledAmount {
        private int total;
    }
}
