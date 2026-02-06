package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Invoice;
import com.solveria.backendservice.infrastructure.repository.InvoiceRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository repository;

    public InvoiceService(InvoiceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Invoice create(String invoiceNumber, LocalDate issuedAt, LocalDate dueAt, BigDecimal amount) {
        Invoice invoice = new Invoice(invoiceNumber, issuedAt, dueAt, amount);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            invoice.setTenantId(tenantId);
        }
        return repository.save(invoice);
    }

    @Transactional(readOnly = true)
    public List<Invoice> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
