package com.space.backend.application.space;

import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingDomainService;
import com.space.backend.domain.booking.BookingRepository;
import com.space.backend.domain.space.Space;
import com.space.backend.domain.space.SpaceCategoryRepository;
import com.space.backend.domain.space.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SpaceCategoryRepository categoryRepository;
    private final SpaceRepository spaceRepository;
    private final BookingRepository bookingRepository;
    private final BookingDomainService bookingDomainService;

    @Override
    @Transactional(readOnly = true)
    public CategoriesResponse getCategories() {
        return new CategoriesResponse(
                categoryRepository.findAllActive().stream().map(CategoryDto::from).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceListResponse getSpaces(UUID categoryId) {
        List<Space> spaces = categoryId != null
                ? spaceRepository.findActiveByCategoryId(categoryId)
                : spaceRepository.findAll();
        return new SpaceListResponse(spaces.stream().map(SpaceSummaryDto::from).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceDetailResponse getSpaceById(UUID id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Space not found: " + id));
        return SpaceDetailResponse.from(space);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableDatesResponse getAvailableDates(UUID spaceId, int year, int month) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found: " + spaceId));
        YearMonth ym = YearMonth.of(year, month);
        Instant monthStart = ym.atDay(1).atStartOfDay(SEOUL).toInstant();
        Instant monthEnd = ym.plusMonths(1).atDay(1).atStartOfDay(SEOUL).toInstant();
        List<Booking> bookings = bookingRepository.findBySpaceAndMonth(spaceId, monthStart, monthEnd);
        Map<LocalDate, Boolean> dates = bookingDomainService.calculateAvailableDates(space, year, month, bookings);
        return new AvailableDatesResponse(year, month, dates);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableSlotsResponse getAvailableSlots(UUID spaceId, LocalDate date) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found: " + spaceId));
        Instant from = date.atStartOfDay(SEOUL).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(SEOUL).toInstant();
        List<Booking> bookings = bookingRepository.findBySpaceAndDateRange(spaceId, from, to);
        return new AvailableSlotsResponse(date,
                bookingDomainService.calculateAvailableSlots(space, date, bookings));
    }
}
