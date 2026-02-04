package com.solveria.iamservice.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;

public record AssignPermissionsToRoleRequest(
        @NotNull(message = "{validation.role.id.required}")
        Long roleId,
        
        @NotEmpty(message = "{validation.permission.ids.required}")
        Collection<@NotNull Long> permissionIds
) {
}
