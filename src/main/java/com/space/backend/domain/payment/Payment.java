package com.space.backend.domain.payment;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    @Column(nullable = false)
    private int amountKrw;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false, unique = true)
    private String pgOrderId;

    private String pgTransactionId;

    private Integer refundAmountKrw;

    @Column(columnDefinition = "TEXT")
    private String refundReason;

    private Instant paidAt;

    private Instant refundedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = Instant.now();
    }

    public void approve(String pgTransactionId) {
        this.pgTransactionId = pgTransactionId;
        this.status = PaymentStatus.APPROVED;
        this.paidAt = Instant.now();
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void refund(int refundAmountKrw, String reason) {
        this.refundAmountKrw = refundAmountKrw;
        this.refundReason = reason;
        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = Instant.now();
    }
}
