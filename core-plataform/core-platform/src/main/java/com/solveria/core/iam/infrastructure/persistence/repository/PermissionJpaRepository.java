package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface PermissionJpaRepository extends JpaRepository<Permission, Long>,
        JpaSpecificationExecutor<Permission> {
    void deleteByRoleId(Long roleId);

    Set<Permission> findByRoleId(Long roleId);


}
