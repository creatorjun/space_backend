// src/main/java/com/space/backend/presentation/admin/AdminBookingListResponse.java
package com.space.backend.presentation.admin;

import com.space.backend.application.admin.AdminBookingListResult;

import java.util.List;

public record AdminBookingListResponse(
        List<AdminBookingResponse> bookings,
        int total,
        int page,
        int size
) {
    public static AdminBookingListResponse from(AdminBookingListResult result) {
        return new AdminBookingListResponse(
                result.bookings().stream().map(AdminBookingResponse::from).toList(),
                result.total(),
                result.page(),
                result.size()
        );
    }
}
