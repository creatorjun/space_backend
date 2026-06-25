package com.space.backend.infrastructure.persistence.payment;

import com.space.backend.domain.payment.Payment;
import com.space.backend.domain.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPgOrderId(String pgOrderId);
    Optional<Payment> findByBookingId(UUID bookingId);
    List<Payment> findByStatus(PaymentStatus status);
}
