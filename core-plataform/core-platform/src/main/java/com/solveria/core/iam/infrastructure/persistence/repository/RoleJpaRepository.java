package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleJpaRepository extends JpaRepository<Role, Long>,
        JpaSpecificationExecutor<Role> {

}

