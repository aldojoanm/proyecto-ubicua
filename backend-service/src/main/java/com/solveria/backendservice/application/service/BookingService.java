package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Booking;
import com.solveria.backendservice.infrastructure.repository.BookingRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Booking create(String referenceCode, Long tripId, BigDecimal totalAmount, String currency) {
        Booking booking = new Booking(referenceCode, tripId, totalAmount, currency);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            booking.setTenantId(tenantId);
        }
        return repository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
