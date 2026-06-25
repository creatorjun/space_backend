package com.space.backend.domain.space;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "spaces")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private SpaceCategory category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int minHours;

    @Column(nullable = false)
    private int maxHours;

    @Column(nullable = false)
    private int pricePerHour;

    private String thumbnailUrl;

    @Column(nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpaceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpaceOperatingHours> operatingHours = new ArrayList<>();

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpaceClosedDay> closedDays = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        isActive = true;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = Instant.now();
    }

    public void update(SpaceCategory category, String name, String description, String address,
                       int capacity, int minHours, int maxHours, int pricePerHour,
                       String thumbnailUrl, int displayOrder, boolean isActive) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.address = address;
        this.capacity = capacity;
        this.minHours = minHours;
        this.maxHours = maxHours;
        this.pricePerHour = pricePerHour;
        this.thumbnailUrl = thumbnailUrl;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }
}
