// src/main/java/com/space/backend/application/admin/AdminBookingListResult.java
package com.space.backend.application.admin;

import java.util.List;

public record AdminBookingListResult(
        List<AdminBookingResult> bookings,
        int total,
        int page,
        int size
) {}
