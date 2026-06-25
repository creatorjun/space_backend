// src/main/java/com/space/backend/application/payment/PaymentServiceImpl.java
package com.space.backend.application.payment;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingRepository;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.exception.EntityNotFoundException;
import com.space.backend.domain.exception.InvalidStatusException;
import com.space.backend.domain.exception.PaymentException;
import com.space.backend.domain.exception.UnauthorizedAccessException;
import com.space.backend.domain.payment.Payment;
import com.space.backend.domain.payment.PaymentProvider;
import com.space.backend.domain.payment.PaymentRepository;
import com.space.backend.domain.payment.PaymentStatus;
import com.space.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("naverPayGateway") private final PaymentGateway naverPayGateway;
    @Qualifier("kakaoPayGateway") private final PaymentGateway kakaoPayGateway;

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

        PaymentGatewayReadyResult result = naverPayGateway.ready(new PaymentGatewayReadyCommand(
                pgOrderId, userId.toString(), booking.getSpace().getName(), booking.getTotalPrice()
        ));

        return new PaymentReadyResponse(pgOrderId, result.redirectUrl(), "NAVER_PAY");
    }

    @Override
    @Transactional
    public void approveNaverPay(String pgOrderId, String paymentId) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        try {
            PaymentGatewayApproveResult result = naverPayGateway.approve(new PaymentGatewayApproveCommand(
                    pgOrderId, null, payment.getUser().getId().toString(), paymentId
            ));
            payment.approve(result.transactionId());
            paymentRepository.save(payment);
            confirmBooking(payment);
        } catch (RuntimeException e) {
            payment.fail();
            paymentRepository.save(payment);
            handleBookingFailure(payment);
            throw e;
        }
    }

    @Override
    @Transactional
    public void handleNaverPayFailure(String pgOrderId) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        payment.fail();
        paymentRepository.save(payment);
        handleBookingFailure(payment);
    }

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

        PaymentGatewayReadyResult result = kakaoPayGateway.ready(new PaymentGatewayReadyCommand(
                pgOrderId, userId.toString(), booking.getSpace().getName(), booking.getTotalPrice()
        ));

        if (result.tid() != null) {
            payment.saveTid(result.tid());
            paymentRepository.save(payment);
        }

        return new PaymentReadyResponse(pgOrderId, result.redirectUrl(), "KAKAO_PAY");
    }

    @Override
    @Transactional
    public void approveKakaoPay(String pgOrderId, String pgToken) {
        Payment payment = getPaymentByOrderId(pgOrderId);
        PaymentGatewayApproveResult result = kakaoPayGateway.approve(new PaymentGatewayApproveCommand(
                pgOrderId, payment.getPgTransactionId(), payment.getUser().getId().toString(), pgToken
        ));
        payment.approve(result.transactionId());
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

    private Booking getBookingForPayment(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> EntityNotFoundException.booking(bookingId));
        if (!booking.getUser().getId().equals(userId)) {
            throw UnauthorizedAccessException.noPermission();
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw PaymentException.notPayable();
        }
        return booking;
    }

    private Payment getPaymentByOrderId(String pgOrderId) {
        return paymentRepository.findByPgOrderId(pgOrderId)
                .orElseThrow(() -> PaymentException.notFound(pgOrderId));
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
}
