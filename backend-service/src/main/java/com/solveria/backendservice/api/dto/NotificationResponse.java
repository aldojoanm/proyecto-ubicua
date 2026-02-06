package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.NotificationChannel;
import com.solveria.backendservice.domain.model.NotificationStatus;

public record NotificationResponse(
        Long id,
        NotificationChannel channel,
        String recipient,
        String message,
        NotificationStatus status
) {
}
