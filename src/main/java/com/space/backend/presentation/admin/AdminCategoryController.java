package com.space.backend.presentation.admin;

import com.space.backend.application.admin.AdminSpaceService;
import com.space.backend.application.admin.CreateCategoryCommand;
import com.space.backend.application.space.CategoryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminSpaceService adminSpaceService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminSpaceService.createCategory(
                        new CreateCategoryCommand(request.name(), request.displayOrder())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(adminSpaceService.updateCategory(
                id, new CreateCategoryCommand(request.name(), request.displayOrder())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminSpaceService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
