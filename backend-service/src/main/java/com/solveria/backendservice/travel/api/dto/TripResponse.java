package com.solveria.backendservice.travel.api.dto;

import com.solveria.backendservice.travel.domain.model.TripStatus;

import java.time.LocalDate;

public record TripResponse(
        Long id,
        String title,
        String origin,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        int travelersCount,
        TripStatus status,
        java.math.BigDecimal budget
) {
}
