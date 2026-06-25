package com.space.backend.presentation.payment;

import com.space.backend.application.payment.PaymentReadyResponse;
import com.space.backend.application.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ── 네이버페이 ──────────────────────────────────────

    @PostMapping("/naver/ready")
    public ResponseEntity<PaymentReadyResponse> naverReady(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody PaymentReadyRequest request) {
        return ResponseEntity.ok(
                paymentService.readyNaverPay(UUID.fromString(userIdStr), request.bookingId()));
    }

    @GetMapping("/naver/approve")
    public ResponseEntity<Void> naverApprove(
            @RequestParam String pgOrderId,
            @RequestParam String paymentId) {
        paymentService.approveNaverPay(pgOrderId, paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/naver/fail")
    public ResponseEntity<Void> naverFail(@RequestParam String pgOrderId) {
        paymentService.handleNaverPayFailure(pgOrderId);
        return ResponseEntity.noContent().build();
    }

    // ── 카카오페이 ──────────────────────────────────────

    @PostMapping("/kakao/ready")
    public ResponseEntity<PaymentReadyResponse> kakaoReady(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody PaymentReadyRequest request) {
        return ResponseEntity.ok(
                paymentService.readyKakaoPay(UUID.fromString(userIdStr), request.bookingId()));
    }

    @GetMapping("/kakao/approve")
    public ResponseEntity<Void> kakaoApprove(
            @RequestParam String pgOrderId,
            @RequestParam String pgToken) {
        paymentService.approveKakaoPay(pgOrderId, pgToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/kakao/cancel")
    public ResponseEntity<Void> kakaoCancel(@RequestParam String pgOrderId) {
        paymentService.handleKakaoPayCancelOrFail(pgOrderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/kakao/fail")
    public ResponseEntity<Void> kakaoFail(@RequestParam String pgOrderId) {
        paymentService.handleKakaoPayCancelOrFail(pgOrderId);
        return ResponseEntity.noContent().build();
    }
}
