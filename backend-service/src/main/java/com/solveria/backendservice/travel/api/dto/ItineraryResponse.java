package com.solveria.backendservice.travel.api.dto;

import java.time.Instant;

public record ItineraryResponse(
        Long tripId,
        int version,
        String content,
        String model,
        int tokensUsed,
        Instant generatedAt
) {
}
