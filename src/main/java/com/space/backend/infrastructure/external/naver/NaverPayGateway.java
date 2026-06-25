// src/main/java/com/space/backend/infrastructure/external/naver/NaverPayGateway.java
package com.space.backend.infrastructure.external.naver;

import com.space.backend.application.payment.PaymentGateway;
import com.space.backend.application.payment.PaymentGatewayApproveCommand;
import com.space.backend.application.payment.PaymentGatewayApproveResult;
import com.space.backend.application.payment.PaymentGatewayReadyCommand;
import com.space.backend.application.payment.PaymentGatewayReadyResult;
import com.space.backend.application.payment.PaymentGatewayRefundCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("naverPayGateway")
@RequiredArgsConstructor
public class NaverPayGateway implements PaymentGateway {

    private final NaverPayClient naverPayClient;

    @Override
    public PaymentGatewayReadyResult ready(PaymentGatewayReadyCommand command) {
        NaverPayReadyResponse resp = naverPayClient.ready(
                command.pgOrderId(),
                command.amount(),
                command.itemName(),
                "/api/payments/naver/approve?pgOrderId=" + command.pgOrderId(),
                command.userId()
        );
        String redirectUrl = resp.getBody() != null ? resp.getBody().getCheckoutPage() : "";
        return new PaymentGatewayReadyResult(redirectUrl, null);
    }

    @Override
    public PaymentGatewayApproveResult approve(PaymentGatewayApproveCommand command) {
        NaverPayApproveResponse resp = naverPayClient.approve(
                command.pgOrderId(),
                command.pgToken()
        );
        if (!"Success".equals(resp.getCode())) {
            throw new RuntimeException("네이버페이 승인 실패: " + resp.getMessage());
        }
        String txId = resp.getBody() != null ? resp.getBody().getPaymentId() : command.pgToken();
        return new PaymentGatewayApproveResult(txId);
    }

    @Override
    public void refund(PaymentGatewayRefundCommand command) {
        naverPayClient.refund(
                command.pgTransactionId(),
                command.cancelAmount(),
                command.reason()
        );
    }
}
