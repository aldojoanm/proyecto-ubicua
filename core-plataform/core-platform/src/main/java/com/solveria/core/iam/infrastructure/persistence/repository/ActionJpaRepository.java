package com.solveria.core.iam.infrastructure.persistence.repository;

import com.solveria.core.iam.domain.model.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionJpaRepository extends JpaRepository<Action, Long>,
        JpaSpecificationExecutor<Action> {
}
