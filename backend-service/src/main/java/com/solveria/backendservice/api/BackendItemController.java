package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.BackendItemResponse;
import com.solveria.backendservice.api.dto.CreateBackendItemRequest;
import com.solveria.backendservice.application.service.BackendItemService;
import com.solveria.backendservice.domain.model.BackendItem;
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
    public BackendItemResponse create(@Valid @RequestBody CreateBackendItemRequest request) {
        BackendItem item = service.create(request.name(), request.description());
        return new BackendItemResponse(item.getId(), item.getName(), item.getDescription());
    }

    @GetMapping
    public List<BackendItemResponse> list() {
        return service.listForTenant().stream()
                .map(item -> new BackendItemResponse(item.getId(), item.getName(), item.getDescription()))
                .toList();
    }
}
