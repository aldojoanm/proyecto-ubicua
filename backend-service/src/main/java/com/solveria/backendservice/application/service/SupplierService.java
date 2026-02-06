package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Supplier;
import com.solveria.backendservice.infrastructure.repository.SupplierRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository repository;

    public SupplierService(SupplierRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Supplier create(String name, String serviceType, String contactEmail) {
        Supplier supplier = new Supplier(name, serviceType, contactEmail);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            supplier.setTenantId(tenantId);
        }
        return repository.save(supplier);
    }

    @Transactional(readOnly = true)
    public List<Supplier> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
