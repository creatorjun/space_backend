package com.space.backend.infrastructure.external.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoPayClient {

    private final RestClient restClient;
    private final KakaoPayProperties props;

    public KakaoPayReadyResponse ready(String pgOrderId, String userId,
                                       String itemName, int amount) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", props.getCid());
        body.add("partner_order_id", pgOrderId);
        body.add("partner_user_id", userId);
        body.add("item_name", itemName);
        body.add("quantity", "1");
        body.add("total_amount", String.valueOf(amount));
        body.add("tax_free_amount", "0");
        body.add("approval_url", props.getApprovalUrl() + "?pg_order_id=" + pgOrderId);
        body.add("cancel_url", props.getCancelUrl() + "?pg_order_id=" + pgOrderId);
        body.add("fail_url", props.getFailUrl() + "?pg_order_id=" + pgOrderId);

        return restClient.post()
                .uri(KakaoPayProperties.BASE_URL + "/ready")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "SECRET_KEY " + props.getSecretKey())
                .body(body)
                .retrieve()
                .body(KakaoPayReadyResponse.class);
    }

    public KakaoPayApproveResponse approve(String tid, String pgOrderId,
                                           String userId, String pgToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", props.getCid());
        body.add("tid", tid);
        body.add("partner_order_id", pgOrderId);
        body.add("partner_user_id", userId);
        body.add("pg_token", pgToken);

        return restClient.post()
                .uri(KakaoPayProperties.BASE_URL + "/approve")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "SECRET_KEY " + props.getSecretKey())
                .body(body)
                .retrieve()
                .body(KakaoPayApproveResponse.class);
    }

    public KakaoPayCancelResponse cancel(String tid, int cancelAmount, String reason) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", props.getCid());
        body.add("tid", tid);
        body.add("cancel_amount", String.valueOf(cancelAmount));
        body.add("cancel_tax_free_amount", "0");
        body.add("cancel_reason", reason);

        return restClient.post()
                .uri(KakaoPayProperties.BASE_URL + "/cancel")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "SECRET_KEY " + props.getSecretKey())
                .body(body)
                .retrieve()
                .body(KakaoPayCancelResponse.class);
    }
}
