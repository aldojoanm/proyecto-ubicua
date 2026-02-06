package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.ReviewTargetType;

public record ReviewResponse(
        Long id,
        String subject,
        int rating,
        String comment,
        ReviewTargetType targetType,
        String targetId
) {
}
