package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.BackendItem;
import com.solveria.backendservice.infrastructure.repository.BackendItemRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BackendItemService {

    private final BackendItemRepository repository;

    public BackendItemService(BackendItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public BackendItem create(String name, String description) {
        BackendItem item = new BackendItem(name, description);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            item.setTenantId(tenantId);
        }
        return repository.save(item);
    }

    @Transactional(readOnly = true)
    public List<BackendItem> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
