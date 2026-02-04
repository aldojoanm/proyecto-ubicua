package com.solveria.backendservice.domain.model;

import com.solveria.backendservice.config.tenant.TenantEntityListener;
import com.solveria.core.shared.base.BaseEntity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(TenantEntityListener.class)
public abstract class TenantAwareEntity extends BaseEntity {
}
