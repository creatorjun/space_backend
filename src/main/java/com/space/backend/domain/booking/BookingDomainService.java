package com.space.backend.domain.booking;

import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceClosedDay;
import com.space.backend.domain.space.SpaceOperatingHours;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class BookingDomainService {

    /**
     * 월 단위로 예약 가능 날짜 맵 반환 (key: LocalDate, value: true=가능)
     */
    public Map<LocalDate, Boolean> calculateAvailableDates(
            Space space, int year, int month, List<Booking> existingBookings) {

        Map<LocalDate, Boolean> result = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.isBefore(today)) {
                result.put(date, false);
                continue;
            }
            result.put(date, isDayAvailable(space, date, existingBookings));
        }
        return result;
    }

    /**
     * 일 단위로 가능한 시간 슬롯 리스트 반환
     */
    public List<TimeSlot> calculateAvailableSlots(
            Space space, LocalDate date, List<Booking> existingBookings) {

        List<TimeSlot> slots = new ArrayList<>();
        SpaceOperatingHours hours = getOperatingHours(space, date);
        if (hours == null || hours.isClosed()) return slots;

        LocalTime open = hours.getOpenTime();
        LocalTime close = hours.getCloseTime();
        if (open == null || close == null) return slots;

        ZoneId zone = ZoneId.of("Asia/Seoul");
        for (LocalTime t = open; t.plusHours(space.getMinHours()).compareTo(close) <= 0;
             t = t.plusHours(1)) {
            Instant slotStart = date.atTime(t).atZone(zone).toInstant();
            Instant slotEnd = date.atTime(t.plusHours(space.getMinHours())).atZone(zone).toInstant();
            boolean available = existingBookings.stream()
                    .noneMatch(b -> b.getStartAt().isBefore(slotEnd) && b.getEndAt().isAfter(slotStart));
            slots.add(new TimeSlot(t, t.plusHours(space.getMinHours()), available));
        }
        return slots;
    }

    public void validateBookingRequest(Space space, Instant startAt, int hours, int headcount) {
        if (hours < space.getMinHours() || hours > space.getMaxHours()) {
            throw new IllegalArgumentException(
                    String.format("예약 가능 시간: %d~%d시간", space.getMinHours(), space.getMaxHours()));
        }
        if (headcount < 1 || headcount > space.getCapacity()) {
            throw new IllegalArgumentException(
                    String.format("수용 인원: 1~%d명", space.getCapacity()));
        }
    }

    public CancelPreview calculateCancelPreview(Booking booking, Instant now) {
        int original = booking.getTotalPrice();
        long hoursUntilStart = Duration.between(now, booking.getStartAt()).toHours();

        if (hoursUntilStart >= 48) {
            return new CancelPreview(original, original, 0, "전액 환불", true);
        } else if (hoursUntilStart >= 24) {
            int penalty = original / 2;
            return new CancelPreview(original, original - penalty, penalty, "50% 위약금", true);
        } else if (hoursUntilStart > 0) {
            return new CancelPreview(original, 0, original, "100% 위약금", true);
        } else {
            return new CancelPreview(original, 0, original, "이용 시작 후 취소 불가", false);
        }
    }

    public int calculateTotalPrice(int pricePerHour, int hours) {
        return pricePerHour * hours;
    }

    // --- private helpers ---

    private boolean isDayAvailable(Space space, LocalDate date, List<Booking> bookings) {
        boolean isClosedDay = space.getClosedDays().stream()
                .map(SpaceClosedDay::getClosedDate)
                .anyMatch(date::equals);
        if (isClosedDay) return false;

        SpaceOperatingHours hours = getOperatingHours(space, date);
        if (hours == null || hours.isClosed()) return false;

        ZoneId zone = ZoneId.of("Asia/Seoul");
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        long bookedHours = bookings.stream()
                .filter(b -> b.getStartAt().isBefore(dayEnd) && b.getEndAt().isAfter(dayStart))
                .mapToLong(b -> Duration.between(
                        b.getStartAt().isBefore(dayStart) ? dayStart : b.getStartAt(),
                        b.getEndAt().isAfter(dayEnd) ? dayEnd : b.getEndAt()
                ).toHours())
                .sum();

        if (hours.getOpenTime() == null || hours.getCloseTime() == null) return false;
        long totalHours = Duration.between(hours.getOpenTime(), hours.getCloseTime()).toHours();
        return (totalHours - bookedHours) >= space.getMinHours();
    }

    private SpaceOperatingHours getOperatingHours(Space space, LocalDate date) {
        int dow = date.getDayOfWeek().getValue() % 7; // 0=일, 1=월 ... 6=토
        return space.getOperatingHours().stream()
                .filter(h -> h.getDayOfWeek() == dow)
                .findFirst()
                .orElse(null);
    }
}
