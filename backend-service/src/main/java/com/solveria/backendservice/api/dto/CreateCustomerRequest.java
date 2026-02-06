package com.solveria.backendservice.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Email @Size(max = 200) String email,
        @Size(max = 40) String phone
) {
}
