package com.space.backend.presentation.admin;

import com.space.backend.application.admin.*;
import com.space.backend.domain.booking.BookingStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @GetMapping
    public ResponseEntity<AdminBookingListResponse> list(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        BookingSearchCondition cond = new BookingSearchCondition(status, date, page, size);
        return ResponseEntity.ok(adminBookingService.getBookings(cond));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminBookingDetailResponse> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(adminBookingService.getBookingById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        adminBookingService.updateBookingStatus(id,
                new UpdateBookingStatusCommand(request.status(), request.adminMemo()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Void> refund(
            @PathVariable UUID id,
            @Valid @RequestBody RefundRequest request) {
        adminBookingService.approveRefund(id,
                new ApproveRefundCommand(request.refundAmountKrw(), request.reason()));
        return ResponseEntity.noContent().build();
    }
}
