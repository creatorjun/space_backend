package com.space.backend.application.payment;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingRepository;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.payment.*;
import com.space.backend.domain.user.User;
import com.space.backend.domain.user.UserRepository;
import com.space.backend.infrastructure.external.kakao.KakaoPayApproveResponse;
import com.space.backend.infrastructure.external.kakao.KakaoPayClient;
import com.space.backend.infrastructure.external.kakao.KakaoPayReadyResponse;
import com.space.backend.infrastructure.external.naver.NaverPayApproveResponse;
import com.space.backend.infrastructure.external.naver.NaverPayClient;
import com.space.backend.infrastructure.external.naver.NaverPayReadyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NaverPayClient naverPayClient;
    private final KakaoPayClient kakaoPayClient;

    // ── 네이버페이 ──────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentReadyResponse readyNaverPay(UUID userId, UUID bookingId) {
        Booking booking = getBookingForPayment(bookingId, userId);
        String pgOrderId = "NP-" + UUID.randomUUID();

        Payment payment = Payment.builder()
                .booking(booking)
                .user(booking.getUser())
                .provider(PaymentProvider.NAVER_PAY)
                .amountKrw(booking.getTotalPrice())
                .status(PaymentStatus.READY)
                .pgOrderId(pgOrderId)
                .build();
        paymentRepository.save(payment);

        NaverPayReadyResponse resp = naverPayClient.ready(
                pgOrderId,
                booking.getTotalPrice(),
                booking.getSpace().getName(),
                "/api/payments/naver/approve?pgOrderId=" + pgOrderId,
                userId.toString()
        );

        String redirectUrl = resp.getBody() != null ? resp.getBody().getCheckoutPage() : "";
        return new PaymentReadyResponse(pgOrderId, redirectUrl, "NAVER_PAY");
    }

    @Override
    @Transactional
    public void approveNaverPay(String pgOrderId, String paymentId) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        NaverPayApproveResponse resp = naverPayClient.approve(pgOrderId, paymentId);

        if (!"Success".equals(resp.getCode())) {
            payment.fail();
            paymentRepository.save(payment);
            handleBookingFailure(payment);
            throw new RuntimeException("네이버페이 승인 실패: " + resp.getMessage());
        }

        String txId = resp.getBody() != null ? resp.getBody().getPaymentId() : paymentId;
        payment.approve(txId);
        paymentRepository.save(payment);
        confirmBooking(payment);
    }

    @Override
    @Transactional
    public void handleNaverPayFailure(String pgOrderId) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        payment.fail();
        paymentRepository.save(payment);
        handleBookingFailure(payment);
    }

    // ── 카카오페이 ──────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentReadyResponse readyKakaoPay(UUID userId, UUID bookingId) {
        Booking booking = getBookingForPayment(bookingId, userId);
        String pgOrderId = "KP-" + UUID.randomUUID();

        Payment payment = Payment.builder()
                .booking(booking)
                .user(booking.getUser())
                .provider(PaymentProvider.KAKAO_PAY)
                .amountKrw(booking.getTotalPrice())
                .status(PaymentStatus.READY)
                .pgOrderId(pgOrderId)
                .build();
        paymentRepository.save(payment);

        KakaoPayReadyResponse resp = kakaoPayClient.ready(
                pgOrderId,
                userId.toString(),
                booking.getSpace().getName(),
                booking.getTotalPrice()
        );

        // tid 를 pgTransactionId 임시 저장 (approve 시 필요)
        saveTid(payment, resp.getTid());

        String redirectUrl = resp.getNextRedirectMobileUrl() != null
                ? resp.getNextRedirectMobileUrl() : resp.getNextRedirectPcUrl();
        return new PaymentReadyResponse(pgOrderId, redirectUrl, "KAKAO_PAY");
    }

    @Override
    @Transactional
    public void approveKakaoPay(String pgOrderId, String pgToken) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        String tid = payment.getPgTransactionId(); // ready 단계에서 저장한 tid

        KakaoPayApproveResponse resp = kakaoPayClient.approve(
                tid, pgOrderId,
                payment.getUser().getId().toString(),
                pgToken
        );

        payment.approve(resp.getTid());
        paymentRepository.save(payment);
        confirmBooking(payment);
    }

    @Override
    @Transactional
    public void handleKakaoPayCancelOrFail(String pgOrderId) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        payment.fail();
        paymentRepository.save(payment);
        handleBookingFailure(payment);
    }

    // ── private helpers ──────────────────────────────────────────

    private Booking getBookingForPayment(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new SecurityException("접근 권한이 없습니다");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다");
        }
        return booking;
    }

    private Payment getPaymentByOrderId(String pgOrderId) {
        return paymentRepository.findByPgOrderId(pgOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + pgOrderId));
    }

    private void confirmBooking(Payment payment) {
        Booking booking = payment.getBooking();
        booking.confirm();
        bookingRepository.save(booking);
    }

    private void handleBookingFailure(Payment payment) {
        Booking booking = payment.getBooking();
        booking.cancelByAdmin("[SYSTEM] 결제 실패 자동 취소");
        bookingRepository.save(booking);
    }

    private void saveTid(Payment payment, String tid) {
        // tid를 pgTransactionId 필드에 임시 저장하기 위한 별도 메서드
        // Payment 엔티티에 updateTid 추가
        payment.saveTid(tid);
        paymentRepository.save(payment);
    }
}
