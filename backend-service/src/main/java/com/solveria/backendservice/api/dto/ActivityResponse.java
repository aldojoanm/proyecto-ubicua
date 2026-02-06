package com.solveria.backendservice.api.dto;

import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        String title,
        String category,
        String location,
        LocalDateTime startsAt,
        LocalDateTime endsAt
) {
}
