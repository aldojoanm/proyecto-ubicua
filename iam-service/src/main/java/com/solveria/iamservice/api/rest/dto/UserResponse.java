package com.solveria.iamservice.api.rest.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        boolean active
) {
}
