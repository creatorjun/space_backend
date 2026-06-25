// src/main/java/com/space/backend/domain/booking/BookingDomainService.java
package com.space.backend.domain.booking;

import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceClosedDay;
import com.space.backend.domain.space.SpaceOperatingHours;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class BookingDomainService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    public Map<LocalDate, Boolean> calculateAvailableDates(
            Space space, int year, int month, List<Booking> existingBookings) {

        Map<LocalDate, Boolean> result = new LinkedHashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate today = LocalDate.now(SEOUL);

        Map<Integer, SpaceOperatingHours> hoursIndex = buildOperatingHoursIndex(space);
        Set<LocalDate> closedDaySet = buildClosedDaySet(space);
        Map<LocalDate, List<Booking>> bookingsByDate = groupBookingsByDate(existingBookings, start, end);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.isBefore(today)) {
                result.put(date, false);
                continue;
            }
            result.put(date, isDayAvailable(space, date, hoursIndex, closedDaySet,
                    bookingsByDate.getOrDefault(date, List.of())));
        }
        return result;
    }

    public List<TimeSlot> calculateAvailableSlots(
            Space space, LocalDate date, List<Booking> existingBookings) {

        List<TimeSlot> slots = new ArrayList<>();
        Map<Integer, SpaceOperatingHours> hoursIndex = buildOperatingHoursIndex(space);
        SpaceOperatingHours hours = hoursIndex.get(date.getDayOfWeek().getValue() % 7);
        if (hours == null || hours.isClosed()) return slots;

        LocalTime open = hours.getOpenTime();
        LocalTime close = hours.getCloseTime();
        if (open == null || close == null) return slots;

        List<Instant[]> occupiedRanges = existingBookings.stream()
                .map(b -> new Instant[]{b.getStartAt(), b.getEndAt()})
                .collect(Collectors.toList());

        for (LocalTime t = open; t.plusHours(space.getMinHours()).compareTo(close) <= 0;
             t = t.plusHours(1)) {
            Instant slotStart = date.atTime(t).atZone(SEOUL).toInstant();
            Instant slotEnd = date.atTime(t.plusHours(space.getMinHours())).atZone(SEOUL).toInstant();
            boolean available = isSlotAvailable(slotStart, slotEnd, occupiedRanges);
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

    private boolean isDayAvailable(
            Space space, LocalDate date,
            Map<Integer, SpaceOperatingHours> hoursIndex,
            Set<LocalDate> closedDaySet,
            List<Booking> dayBookings) {

        if (closedDaySet.contains(date)) return false;

        SpaceOperatingHours hours = hoursIndex.get(date.getDayOfWeek().getValue() % 7);
        if (hours == null || hours.isClosed()) return false;
        if (hours.getOpenTime() == null || hours.getCloseTime() == null) return false;

        Instant dayStart = date.atStartOfDay(SEOUL).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(SEOUL).toInstant();

        long bookedHours = dayBookings.stream()
                .mapToLong(b -> Duration.between(
                        b.getStartAt().isBefore(dayStart) ? dayStart : b.getStartAt(),
                        b.getEndAt().isAfter(dayEnd) ? dayEnd : b.getEndAt()
                ).toHours())
                .sum();

        long totalHours = Duration.between(hours.getOpenTime(), hours.getCloseTime()).toHours();
        return (totalHours - bookedHours) >= space.getMinHours();
    }

    private boolean isSlotAvailable(Instant slotStart, Instant slotEnd, List<Instant[]> occupiedRanges) {
        for (Instant[] range : occupiedRanges) {
            if (range[0].isBefore(slotEnd) && range[1].isAfter(slotStart)) {
                return false;
            }
        }
        return true;
    }

    private Map<Integer, SpaceOperatingHours> buildOperatingHoursIndex(Space space) {
        Map<Integer, SpaceOperatingHours> index = new HashMap<>();
        for (SpaceOperatingHours h : space.getOperatingHours()) {
            index.put(h.getDayOfWeek(), h);
        }
        return index;
    }

    private Set<LocalDate> buildClosedDaySet(Space space) {
        Set<LocalDate> set = new HashSet<>();
        for (SpaceClosedDay cd : space.getClosedDays()) {
            set.add(cd.getClosedDate());
        }
        return set;
    }

    private Map<LocalDate, List<Booking>> groupBookingsByDate(
            List<Booking> bookings, LocalDate rangeStart, LocalDate rangeEnd) {

        Map<LocalDate, List<Booking>> map = new HashMap<>();
        for (Booking b : bookings) {
            LocalDate bookingDate = b.getStartAt().atZone(SEOUL).toLocalDate();
            if (!bookingDate.isBefore(rangeStart) && !bookingDate.isAfter(rangeEnd)) {
                map.computeIfAbsent(bookingDate, k -> new ArrayList<>()).add(b);
            }
        }
        return map;
    }
}
