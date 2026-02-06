package com.solveria.backendservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateBookingRequest(
        @NotBlank @Size(max = 40) String referenceCode,
        @NotNull Long tripId,
        @NotNull @Positive BigDecimal totalAmount,
        @NotBlank @Size(min = 3, max = 3) String currency
) {
}
