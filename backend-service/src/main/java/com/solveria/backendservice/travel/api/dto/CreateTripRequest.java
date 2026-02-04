package com.solveria.backendservice.travel.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTripRequest(
        @NotBlank String title,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @Min(1) int travelersCount
) {

        @AssertTrue(message = "startDate must be on or before endDate")
        public boolean isDateRangeValid() {
                if (startDate == null || endDate == null) {
                        return false;
                }
                return !startDate.isAfter(endDate);
        }
}
