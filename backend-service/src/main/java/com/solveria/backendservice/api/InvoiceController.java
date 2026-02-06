package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.CreateInvoiceRequest;
import com.solveria.backendservice.api.dto.InvoiceResponse;
import com.solveria.backendservice.application.service.InvoiceService;
import com.solveria.backendservice.domain.model.Invoice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create invoice",
            description = "Creates an invoice. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create invoice payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateInvoiceRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "invoiceNumber": "INV-2026-001",
                                      "issuedAt": "2026-04-01",
                                      "dueAt": "2026-04-15",
                                      "amount": 999.99
                                    }
                                    """
                    )
            )
    )
    public InvoiceResponse create(@Valid @RequestBody CreateInvoiceRequest request) {
        Invoice invoice = service.create(request.invoiceNumber(), request.issuedAt(), request.dueAt(), request.amount());
        return new InvoiceResponse(invoice.getId(), invoice.getInvoiceNumber(), invoice.getIssuedAt(), invoice.getDueAt(), invoice.getAmount(), invoice.getStatus());
    }

    @GetMapping
    @Operation(
            summary = "List invoices",
            description = "Lists invoices for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<InvoiceResponse> list() {
        return service.listForTenant().stream()
                .map(i -> new InvoiceResponse(i.getId(), i.getInvoiceNumber(), i.getIssuedAt(), i.getDueAt(), i.getAmount(), i.getStatus()))
                .toList();
    }
}
