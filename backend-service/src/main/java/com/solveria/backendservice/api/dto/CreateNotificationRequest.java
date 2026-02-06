package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(
        @NotNull NotificationChannel channel,
        @NotBlank @Size(max = 200) String recipient,
        @NotBlank @Size(max = 500) String message
) {
}
