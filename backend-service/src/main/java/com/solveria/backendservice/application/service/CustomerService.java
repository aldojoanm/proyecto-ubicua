package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Customer;
import com.solveria.backendservice.infrastructure.repository.CustomerRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Customer create(String fullName, String email, String phone) {
        Customer customer = new Customer(fullName, email, phone);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            customer.setTenantId(tenantId);
        }
        return repository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
