package com.space.backend.application.admin;

import java.util.List;

public record AdminBookingListResponse(
        List<AdminBookingDto> bookings,
        int totalCount,
        int page,
        int size
) {}
