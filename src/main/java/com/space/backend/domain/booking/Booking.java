package com.space.backend.domain.booking;

import com.space.backend.domain.space.Space;
import com.space.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant endAt;

    @Column(nullable = false)
    private int hours;

    @Column(nullable = false)
    private int headcount;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status;

    private Instant pendingExpiresAt;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(columnDefinition = "TEXT")
    private String adminMemo;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == null) status = BookingStatus.PENDING;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = Instant.now();
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void requestCancel() {
        this.status = BookingStatus.CANCEL_REQUESTED;
    }

    public void cancelByUser() {
        this.status = BookingStatus.CANCELLED_BY_USER;
    }

    public void cancelByAdmin(String adminMemo) {
        this.status = BookingStatus.CANCELLED_BY_ADMIN;
        this.adminMemo = adminMemo;
    }

    public void expire() {
        this.status = BookingStatus.CANCELLED_BY_ADMIN;
    }

    public void updateAdminMemo(String adminMemo) {
        this.adminMemo = adminMemo;
    }
}
