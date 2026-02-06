package com.solveria.backendservice.application.service;

import com.solveria.backendservice.domain.model.Review;
import com.solveria.backendservice.domain.model.ReviewTargetType;
import com.solveria.backendservice.infrastructure.repository.ReviewRepository;
import com.solveria.core.security.context.SecurityTenantContext;
import com.solveria.core.shared.specifications.TenantSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Review create(String subject, int rating, String comment, ReviewTargetType targetType, String targetId) {
        Review review = new Review(subject, rating, comment, targetType, targetId);
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            review.setTenantId(tenantId);
        }
        return repository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> listForTenant() {
        String tenantId = SecurityTenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return List.of();
        }
        return repository.findAll(TenantSpecifications.hasTenant(tenantId));
    }
}
