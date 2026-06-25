package com.space.backend.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment")
@Getter
@Setter
public class PaymentProperties {

    private Naver naver = new Naver();
    private Kakao kakao = new Kakao();

    @Getter @Setter
    public static class Naver {
        private String clientId;
        private String clientSecret;
        private String chainId;
        private String env;
    }

    @Getter @Setter
    public static class Kakao {
        private String secretKey;
        private String cid;
        private String approvalUrl;
        private String cancelUrl;
        private String failUrl;
    }
}
