package com.space.backend.presentation.admin;

import com.space.backend.application.admin.*;
import com.space.backend.application.space.SpaceDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/spaces")
@RequiredArgsConstructor
public class AdminSpaceController {

    private final AdminSpaceService adminSpaceService;

    @PostMapping
    public ResponseEntity<SpaceDetailResponse> create(
            @Valid @RequestBody CreateSpaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminSpaceService.createSpace(request.toCommand()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceDetailResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSpaceRequest request) {
        return ResponseEntity.ok(adminSpaceService.updateSpace(id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminSpaceService.deleteSpace(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/operating-hours")
    public ResponseEntity<Void> updateHours(
            @PathVariable UUID id,
            @RequestBody List<OperatingHoursRequest> requests) {
        adminSpaceService.updateOperatingHours(
                id, requests.stream().map(OperatingHoursRequest::toCommand).toList());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/closed-days")
    public ResponseEntity<Void> addClosedDay(
            @PathVariable UUID id,
            @RequestBody ClosedDayRequest request) {
        adminSpaceService.addClosedDay(id, request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/closed-days/{date}")
    public ResponseEntity<Void> removeClosedDay(
            @PathVariable UUID id,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        adminSpaceService.removeClosedDay(id, date);
        return ResponseEntity.noContent().build();
    }
}
