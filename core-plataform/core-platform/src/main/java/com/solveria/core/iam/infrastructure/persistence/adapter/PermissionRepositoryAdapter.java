package com.solveria.core.iam.infrastructure.persistence.adapter;

import com.solveria.core.iam.application.port.PermissionRepositoryPort;
import com.solveria.core.iam.domain.model.Permission;
import com.solveria.core.iam.infrastructure.persistence.repository.PermissionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;

    public PermissionRepositoryAdapter(PermissionJpaRepository permissionJpaRepository) {
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Set<Permission> saveAll(Set<Permission> permissions) {
        return Set.copyOf(permissionJpaRepository.saveAll(permissions));
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        permissionJpaRepository.deleteByRoleId(roleId);
    }

    @Override
    public Set<Permission> findByIds(Set<Long> ids) {
        return Set.copyOf(permissionJpaRepository.findAllById(ids));
    }
}
