package com.space.backend.domain.payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByPgOrderId(String pgOrderId);
    Optional<Payment> findByBookingId(UUID bookingId);
    List<Payment> findRefundRequested();
}
