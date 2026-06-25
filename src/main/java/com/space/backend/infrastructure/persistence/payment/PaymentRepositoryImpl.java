package com.space.backend.infrastructure.persistence.payment;

import com.space.backend.domain.payment.Payment;
import com.space.backend.domain.payment.PaymentRepository;
import com.space.backend.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpa;

    @Override
    public Payment save(Payment payment) { return jpa.save(payment); }

    @Override
    public Optional<Payment> findByPgOrderId(String pgOrderId) { return jpa.findByPgOrderId(pgOrderId); }

    @Override
    public Optional<Payment> findByBookingId(UUID bookingId) { return jpa.findByBookingId(bookingId); }

    @Override
    public List<Payment> findRefundRequested() { return jpa.findByStatus(PaymentStatus.REFUND_REQUESTED); }
}
