package com.solveria.backendservice.travel.application;

import com.solveria.backendservice.travel.domain.model.Trip;
import com.solveria.backendservice.travel.infrastructure.jpa.TripRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TripService {

    private final TripRepository repository;

    public TripService(TripRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Trip create(String title, String origin, String destination, LocalDate startDate, LocalDate endDate, int travelersCount) {
        String ownerUserId = resolveUserId();
        Trip trip = new Trip(title, origin, destination, startDate, endDate, travelersCount, ownerUserId);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            trip.setTenantId(tenantId);
        }
        return repository.save(trip);
    }

    @Transactional(readOnly = true)
    public List<Trip> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }

    @Transactional(readOnly = true)
    public Trip getForTenant(Long tripId) {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }
        return repository.findOne(TenantSpecifications.hasTenant(tenantId)
                        .and((root, query, cb) -> cb.equal(root.get("id"), tripId)))
                .orElse(null);
    }

    private String resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getSubject();
        }
        return null;
    }
}
