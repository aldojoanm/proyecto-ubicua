package com.solveria.backendservice.infrastructure.repository;

import com.solveria.backendservice.domain.model.BackendItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BackendItemRepository extends JpaRepository<BackendItem, Long>, JpaSpecificationExecutor<BackendItem> {
}
