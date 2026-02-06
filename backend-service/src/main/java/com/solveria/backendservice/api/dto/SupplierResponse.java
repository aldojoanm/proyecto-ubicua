package com.solveria.backendservice.api.dto;

public record SupplierResponse(
        Long id,
        String name,
        String serviceType,
        String contactEmail
) {
}
