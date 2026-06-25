package com.space.backend.infrastructure.external.naver;

import com.space.backend.infrastructure.config.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverPayProperties {
    private final PaymentProperties props;

    public String getClientId()     { return props.getNaver().getClientId(); }
    public String getClientSecret() { return props.getNaver().getClientSecret(); }
    public String getChainId()      { return props.getNaver().getChainId(); }
    public boolean isSandbox()      { return "sandbox".equalsIgnoreCase(props.getNaver().getEnv()); }

    public String getBaseUrl() {
        return isSandbox()
                ? "https://sandbox-pay.naver.com/naverpay/payments/v2.2"
                : "https://pay.naver.com/naverpay/payments/v2.2";
    }
}
