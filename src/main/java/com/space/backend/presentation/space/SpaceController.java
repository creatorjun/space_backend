package com.space.backend.presentation.space;

import com.space.backend.application.space.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @GetMapping("/api/categories")
    public ResponseEntity<CategoriesResponse> getCategories() {
        return ResponseEntity.ok(spaceService.getCategories());
    }

    @GetMapping("/api/spaces")
    public ResponseEntity<SpaceListResponse> getSpaces(
            @RequestParam(required = false) UUID categoryId) {
        return ResponseEntity.ok(spaceService.getSpaces(categoryId));
    }

    @GetMapping("/api/spaces/{id}")
    public ResponseEntity<SpaceDetailResponse> getSpace(@PathVariable UUID id) {
        return ResponseEntity.ok(spaceService.getSpaceById(id));
    }

    @GetMapping("/api/spaces/{id}/available-dates")
    public ResponseEntity<AvailableDatesResponse> getAvailableDates(
            @PathVariable UUID id,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(spaceService.getAvailableDates(id, year, month));
    }

    @GetMapping("/api/spaces/{id}/available-slots")
    public ResponseEntity<AvailableSlotsResponse> getAvailableSlots(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(spaceService.getAvailableSlots(id, date));
    }
}
