package com.solveria.backendservice.travel.api.dto;

public record DestinationQaResponse(
        String answer,
        boolean cached,
        int promptTokens,
        int completionTokens
) {
}
