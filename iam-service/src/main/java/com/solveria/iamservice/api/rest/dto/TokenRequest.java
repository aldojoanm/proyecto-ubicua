package com.solveria.iamservice.api.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TokenRequest(
        @NotNull
        Long userId,

        @NotBlank
        @Size(max = 120)
        String username,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(max = 80)
        String tenantId,

        @NotNull
        List<@NotBlank String> scopes
) {
}
