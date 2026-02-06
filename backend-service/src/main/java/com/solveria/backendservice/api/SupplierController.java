package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.CreateSupplierRequest;
import com.solveria.backendservice.api.dto.SupplierResponse;
import com.solveria.backendservice.application.service.SupplierService;
import com.solveria.backendservice.domain.model.Supplier;
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
@RequestMapping("/api/suppliers")
@Tag(name = "Suppliers")
public class SupplierController {

    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create supplier",
            description = "Creates a supplier. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create supplier payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateSupplierRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "name": "Mountain Tours",
                                      "serviceType": "Excursions",
                                      "contactEmail": "hello@mountain.tours"
                                    }
                                    """
                    )
            )
    )
    public SupplierResponse create(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier supplier = service.create(request.name(), request.serviceType(), request.contactEmail());
        return new SupplierResponse(supplier.getId(), supplier.getName(), supplier.getServiceType(), supplier.getContactEmail());
    }

    @GetMapping
    @Operation(
            summary = "List suppliers",
            description = "Lists suppliers for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<SupplierResponse> list() {
        return service.listForTenant().stream()
                .map(s -> new SupplierResponse(s.getId(), s.getName(), s.getServiceType(), s.getContactEmail()))
                .toList();
    }
}
