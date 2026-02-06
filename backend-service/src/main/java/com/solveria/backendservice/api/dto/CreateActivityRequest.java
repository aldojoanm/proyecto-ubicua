package com.solveria.backendservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateActivityRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 80) String category,
        @NotBlank @Size(max = 140) String location,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt
) {
}
