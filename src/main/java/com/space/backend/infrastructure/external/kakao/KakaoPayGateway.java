// src/main/java/com/space/backend/infrastructure/external/kakao/KakaoPayGateway.java
package com.space.backend.infrastructure.external.kakao;

import com.space.backend.application.payment.PaymentGateway;
import com.space.backend.application.payment.PaymentGatewayApproveCommand;
import com.space.backend.application.payment.PaymentGatewayApproveResult;
import com.space.backend.application.payment.PaymentGatewayReadyCommand;
import com.space.backend.application.payment.PaymentGatewayReadyResult;
import com.space.backend.application.payment.PaymentGatewayRefundCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("kakaoPayGateway")
@RequiredArgsConstructor
public class KakaoPayGateway implements PaymentGateway {

    private final KakaoPayClient kakaoPayClient;

    @Override
    public PaymentGatewayReadyResult ready(PaymentGatewayReadyCommand command) {
        KakaoPayReadyResponse resp = kakaoPayClient.ready(
                command.pgOrderId(),
                command.userId(),
                command.itemName(),
                command.amount()
        );
        String redirectUrl = resp.getNextRedirectMobileUrl() != null
                ? resp.getNextRedirectMobileUrl() : resp.getNextRedirectPcUrl();
        return new PaymentGatewayReadyResult(redirectUrl, resp.getTid());
    }

    @Override
    public PaymentGatewayApproveResult approve(PaymentGatewayApproveCommand command) {
        KakaoPayApproveResponse resp = kakaoPayClient.approve(
                command.tid(),
                command.pgOrderId(),
                command.userId(),
                command.pgToken()
        );
        return new PaymentGatewayApproveResult(resp.getTid());
    }

    @Override
    public void refund(PaymentGatewayRefundCommand command) {
        kakaoPayClient.cancel(
                command.pgTransactionId(),
                command.cancelAmount(),
                command.reason()
        );
    }
}
