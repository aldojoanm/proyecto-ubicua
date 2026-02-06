package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.BookingStatus;

import java.math.BigDecimal;

public record BookingResponse(
        Long id,
        String referenceCode,
        Long tripId,
        BookingStatus status,
        BigDecimal totalAmount,
        String currency
) {
}
