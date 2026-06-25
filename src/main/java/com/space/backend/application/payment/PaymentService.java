package com.space.backend.application.payment;

import java.util.UUID;

public interface PaymentService {
    PaymentReadyResponse readyNaverPay(UUID userId, UUID bookingId);
    void approveNaverPay(String pgOrderId, String paymentId);
    void handleNaverPayFailure(String pgOrderId);

    PaymentReadyResponse readyKakaoPay(UUID userId, UUID bookingId);
    void approveKakaoPay(String pgOrderId, String pgToken);
    void handleKakaoPayCancelOrFail(String pgOrderId);
}
