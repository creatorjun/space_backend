package com.space.backend.application.admin;

import com.space.backend.domain.booking.BookingStatus;

public record UpdateBookingStatusCommand(BookingStatus status, String adminMemo) {}
