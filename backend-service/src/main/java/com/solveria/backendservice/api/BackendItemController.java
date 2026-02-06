package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.BackendItemResponse;
import com.solveria.backendservice.api.dto.CreateBackendItemRequest;
import com.solveria.backendservice.application.service.BackendItemService;
import com.solveria.backendservice.domain.model.BackendItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class BackendItemController {

    private final BackendItemService service;

    public BackendItemController(BackendItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create item",
            description = "Creates a backend item. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create item payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateBackendItemRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "name": "Sample item",
                                      "description": "Created from Swagger"
                                    }
                                    """
                    )
            )
    )
    public BackendItemResponse create(@Valid @RequestBody CreateBackendItemRequest request) {
        BackendItem item = service.create(request.name(), request.description());
        return new BackendItemResponse(item.getId(), item.getName(), item.getDescription());
    }

    @GetMapping
    @Operation(
            summary = "List items",
            description = "Lists backend items for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<BackendItemResponse> list() {
        return service.listForTenant().stream()
                .map(item -> new BackendItemResponse(item.getId(), item.getName(), item.getDescription()))
                .toList();
    }
}
