// src/main/java/com/space/backend/application/admin/AdminBookingServiceImpl.java
package com.space.backend.application.admin;

import com.space.backend.application.payment.PaymentGateway;
import com.space.backend.application.payment.PaymentGatewayRefundCommand;
import com.space.backend.domain.booking.AdminBookingQueryPort;
import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingRepository;
import com.space.backend.domain.booking.BookingStatus;
import com.space.backend.domain.payment.Payment;
import com.space.backend.domain.payment.PaymentProvider;
import com.space.backend.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final AdminBookingQueryPort adminBookingQueryPort;
    @Qualifier("naverPayGateway") private final PaymentGateway naverPayGateway;
    @Qualifier("kakaoPayGateway") private final PaymentGateway kakaoPayGateway;

    @Override
    @Transactional(readOnly = true)
    public AdminBookingListResponse getBookings(BookingSearchCondition condition) {
        List<Booking> bookings = adminBookingQueryPort.findByCondition(condition);
        long total = adminBookingQueryPort.countByCondition(condition);
        List<AdminBookingDto> dtos = bookings.stream().map(AdminBookingDto::from).toList();
        return new AdminBookingListResponse(dtos, (int) total, condition.page(), condition.size());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminBookingDetailResponse getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return new AdminBookingDetailResponse(AdminBookingDto.from(booking));
    }

    @Override
    @Transactional
    public void updateBookingStatus(UUID bookingId, UpdateBookingStatusCommand cmd) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        switch (cmd.status()) {
            case CONFIRMED -> booking.confirm();
            case CANCELLED_BY_ADMIN -> booking.cancelByAdmin(cmd.adminMemo());
            default -> throw new IllegalArgumentException("관리자가 설정할 수 없는 상태: " + cmd.status());
        }
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void approveRefund(UUID bookingId, ApproveRefundCommand cmd) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (booking.getStatus() != BookingStatus.CANCEL_REQUESTED) {
            throw new IllegalArgumentException("취소 신청 상태가 아닙니다");
        }
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        PaymentGateway gateway = payment.getProvider() == PaymentProvider.NAVER_PAY
                ? naverPayGateway : kakaoPayGateway;

        gateway.refund(new PaymentGatewayRefundCommand(
                payment.getPgTransactionId(),
                cmd.refundAmountKrw(),
                cmd.reason()
        ));

        payment.refund(cmd.refundAmountKrw(), cmd.reason());
        paymentRepository.save(payment);
        booking.cancelByUser();
        bookingRepository.save(booking);
    }
}
