package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.ReviewTargetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotBlank @Size(max = 160) String subject,
        @Min(1) @Max(5) int rating,
        @Size(max = 500) String comment,
        @NotNull ReviewTargetType targetType,
        @NotBlank @Size(max = 120) String targetId
) {
}
