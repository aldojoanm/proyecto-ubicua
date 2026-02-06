package com.solveria.backendservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "bookings")
public class Booking extends TenantAwareEntity {

    @Column(name = "reference_code", nullable = false, length = 40)
    private String referenceCode;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BookingStatus status = BookingStatus.CREATED;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    protected Booking() {
    }

    public Booking(String referenceCode, Long tripId, BigDecimal totalAmount, String currency) {
        this.referenceCode = referenceCode;
        this.tripId = tripId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.status = BookingStatus.CREATED;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public Long getTripId() {
        return tripId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }
}
