package com.solveria.backendservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice extends TenantAwareEntity {

    @Column(name = "invoice_number", nullable = false, length = 40)
    private String invoiceNumber;

    @Column(name = "issued_at", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDate dueAt;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.ISSUED;

    protected Invoice() {
    }

    public Invoice(String invoiceNumber, LocalDate issuedAt, LocalDate dueAt, BigDecimal amount) {
        this.invoiceNumber = invoiceNumber;
        this.issuedAt = issuedAt;
        this.dueAt = dueAt;
        this.amount = amount;
        this.status = InvoiceStatus.ISSUED;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public LocalDate getDueAt() {
        return dueAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }
}
