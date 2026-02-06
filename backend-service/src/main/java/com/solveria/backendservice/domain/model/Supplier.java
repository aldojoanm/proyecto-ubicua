package com.solveria.backendservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier extends TenantAwareEntity {

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "service_type", nullable = false, length = 120)
    private String serviceType;

    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    protected Supplier() {
    }

    public Supplier(String name, String serviceType, String contactEmail) {
        this.name = name;
        this.serviceType = serviceType;
        this.contactEmail = contactEmail;
    }

    public String getName() {
        return name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getContactEmail() {
        return contactEmail;
    }
}
