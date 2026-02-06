package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.CreateCustomerRequest;
import com.solveria.backendservice.api.dto.CustomerResponse;
import com.solveria.backendservice.application.service.CustomerService;
import com.solveria.backendservice.domain.model.Customer;
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
@RequestMapping("/api/customers")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create customer",
            description = "Creates a customer. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create customer payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateCustomerRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "fullName": "Ada Lovelace",
                                      "email": "ada@example.com",
                                      "phone": "+56 9 1234 5678"
                                    }
                                    """
                    )
            )
    )
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = service.create(request.fullName(), request.email(), request.phone());
        return new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail(), customer.getPhone(), customer.getStatus());
    }

    @GetMapping
    @Operation(
            summary = "List customers",
            description = "Lists customers for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<CustomerResponse> list() {
        return service.listForTenant().stream()
                .map(c -> new CustomerResponse(c.getId(), c.getFullName(), c.getEmail(), c.getPhone(), c.getStatus()))
                .toList();
    }
}
