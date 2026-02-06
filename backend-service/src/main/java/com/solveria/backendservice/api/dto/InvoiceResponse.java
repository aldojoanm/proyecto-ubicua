package com.solveria.backendservice.api.dto;

import com.solveria.backendservice.domain.model.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        LocalDate issuedAt,
        LocalDate dueAt,
        BigDecimal amount,
        InvoiceStatus status
) {
}
