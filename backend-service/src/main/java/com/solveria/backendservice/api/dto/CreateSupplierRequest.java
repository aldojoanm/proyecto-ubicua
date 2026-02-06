package com.solveria.backendservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSupplierRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 120) String serviceType,
        @Size(max = 200) String contactEmail
) {
}
