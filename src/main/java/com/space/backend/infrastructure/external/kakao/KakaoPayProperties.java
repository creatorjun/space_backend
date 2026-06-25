package com.space.backend.infrastructure.external.kakao;

import com.space.backend.infrastructure.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoPayProperties {
    private final PaymentProperties props;

    public String getSecretKey()   { return props.getKakao().getSecretKey(); }
    public String getCid()         { return props.getKakao().getCid(); }
    public String getApprovalUrl() { return props.getKakao().getApprovalUrl(); }
    public String getCancelUrl()   { return props.getKakao().getCancelUrl(); }
    public String getFailUrl()     { return props.getKakao().getFailUrl(); }

    public static final String BASE_URL = "https://open-api.kakaopay.com/online/v1/payment";
}
