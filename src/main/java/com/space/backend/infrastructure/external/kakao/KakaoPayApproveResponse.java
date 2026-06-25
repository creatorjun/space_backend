package com.space.backend.infrastructure.external.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoPayApproveResponse {
    private String tid;
    @JsonProperty("partner_order_id") private String partnerOrderId;
    @JsonProperty("partner_user_id")  private String partnerUserId;
    @JsonProperty("amount")           private Amount amount;
    @JsonProperty("approved_at")      private String approvedAt;

    @Getter @NoArgsConstructor
    public static class Amount {
        private int total;
    }
}
