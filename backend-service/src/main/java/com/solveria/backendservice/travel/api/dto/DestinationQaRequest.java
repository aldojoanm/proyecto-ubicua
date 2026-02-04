package com.solveria.backendservice.travel.api.dto;

import jakarta.validation.constraints.NotBlank;

public record DestinationQaRequest(
        @NotBlank String question,
        Long tripId
) {
}
