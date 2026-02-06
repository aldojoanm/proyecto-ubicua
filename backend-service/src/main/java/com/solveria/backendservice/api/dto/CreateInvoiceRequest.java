package com.solveria.backendservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInvoiceRequest(
        @NotBlank @Size(max = 40) String invoiceNumber,
        @NotNull LocalDate issuedAt,
        @NotNull LocalDate dueAt,
        @NotNull @Positive BigDecimal amount
) {
}
