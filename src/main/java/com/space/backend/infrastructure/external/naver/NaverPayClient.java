package com.space.backend.infrastructure.external.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class NaverPayClient {

    private final RestClient restClient;
    private final NaverPayProperties props;

    public NaverPayReadyResponse ready(String pgOrderId, int amount, String productName,
                                       String returnUrl, String userId) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("merchantPayKey", pgOrderId);
        body.add("productName", productName);
        body.add("totalPayAmount", String.valueOf(amount));
        body.add("taxScopeAmount", String.valueOf(amount));
        body.add("taxExScopeAmount", "0");
        body.add("returnUrl", returnUrl);
        body.add("merchantUserKey", userId);

        return restClient.post()
                .uri(props.getBaseUrl() + "/reserve")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("X-Naver-Client-Id", props.getClientId())
                .header("X-Naver-Client-Secret", props.getClientSecret())
                .body(body)
                .retrieve()
                .body(NaverPayReadyResponse.class);
    }

    public NaverPayApproveResponse approve(String pgOrderId, String paymentId) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("merchantPayKey", pgOrderId);
        body.add("paymentId", paymentId);

        return restClient.post()
                .uri(props.getBaseUrl() + "/apply/payment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("X-Naver-Client-Id", props.getClientId())
                .header("X-Naver-Client-Secret", props.getClientSecret())
                .body(body)
                .retrieve()
                .body(NaverPayApproveResponse.class);
    }

    public NaverPayRefundResponse refund(String paymentId, int cancelAmount, String reason) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("paymentId", paymentId);
        body.add("cancelAmount", String.valueOf(cancelAmount));
        body.add("cancelReason", reason);
        body.add("cancelRequester", "2");

        return restClient.post()
                .uri(props.getBaseUrl() + "/cancel")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("X-Naver-Client-Id", props.getClientId())
                .header("X-Naver-Client-Secret", props.getClientSecret())
                .body(body)
                .retrieve()
                .body(NaverPayRefundResponse.class);
    }
}
