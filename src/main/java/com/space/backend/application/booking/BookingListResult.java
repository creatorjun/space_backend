// src/main/java/com/space/backend/application/booking/BookingListResult.java
package com.space.backend.application.booking;

import java.util.List;

public record BookingListResult(List<BookingResult> bookings) {}
