package com.space.backend.presentation.booking;

import com.space.backend.application.booking.*;
import com.space.backend.domain.booking.CancelPreview;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse resp = bookingService.createBooking(
                UUID.fromString(userIdStr), request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/my")
    public ResponseEntity<BookingListResponse> myBookings(
            @AuthenticationPrincipal String userIdStr) {
        return ResponseEntity.ok(
                bookingService.getMyBookings(UUID.fromString(userIdStr)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getOne(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                bookingService.getBookingById(UUID.fromString(userIdStr), id));
    }

    @GetMapping("/{id}/cancel-preview")
    public ResponseEntity<CancelPreview> cancelPreview(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                bookingService.getCancelPreview(UUID.fromString(userIdStr), id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> requestCancel(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID id) {
        bookingService.requestCancel(UUID.fromString(userIdStr), id);
        return ResponseEntity.noContent().build();
    }
}
