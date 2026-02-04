package com.solveria.backendservice.api.dto;

public record BackendItemResponse(
        Long id,
        String name,
        String description
) {
}
