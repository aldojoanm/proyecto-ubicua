package com.solveria.backendservice.travel.domain.model;

import com.solveria.backendservice.domain.model.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "trips")
public class Trip extends TenantAwareEntity {

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @Column(name = "origin", nullable = false, length = 120)
    private String origin;

    @Column(name = "destination", nullable = false, length = 120)
    private String destination;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "travelers_count", nullable = false)
    private int travelersCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private TripStatus status = TripStatus.PLANNED;

    @Column(name = "owner_user_id", length = 120)
    private String ownerUserId;

    @Column(name = "budget", precision = 12, scale = 2)
    private java.math.BigDecimal budget;

    protected Trip() {
    }

    public Trip(String title, String origin, String destination, LocalDate startDate, LocalDate endDate, int travelersCount, String ownerUserId) {
        this.title = title;
        this.origin = origin;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travelersCount = travelersCount;
        this.ownerUserId = ownerUserId;
        this.status = TripStatus.PLANNED;
    }

    public String getTitle() {
        return title;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTravelersCount() {
        return travelersCount;
    }

    public TripStatus getStatus() {
        return status;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public java.math.BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(java.math.BigDecimal budget) {
        this.budget = budget;
    }

    public void markItineraryReady() {
        this.status = TripStatus.ITINERARY_READY;
    }
}
