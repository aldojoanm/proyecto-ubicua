package com.solveria.backendservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity extends TenantAwareEntity {

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @Column(name = "category", nullable = false, length = 80)
    private String category;

    @Column(name = "location", nullable = false, length = 140)
    private String location;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    protected Activity() {
    }

    public Activity(String title, String category, String location, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }
}
