package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.CustomerStatus;

public record CustomerResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        CustomerStatus status
) {
}
