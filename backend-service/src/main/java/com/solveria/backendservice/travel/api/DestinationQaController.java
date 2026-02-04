package com.solveria.backendservice.travel.api;

import com.solveria.backendservice.travel.api.dto.DestinationQaRequest;
import com.solveria.backendservice.travel.api.dto.DestinationQaResponse;
import com.solveria.backendservice.travel.application.DestinationQaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/destinations")
public class DestinationQaController {

    private final DestinationQaService qaService;

    public DestinationQaController(DestinationQaService qaService) {
        this.qaService = qaService;
    }

    @PostMapping("/{destinationId}/qa")
    public DestinationQaResponse ask(@PathVariable String destinationId, @Valid @RequestBody DestinationQaRequest request) {
        DestinationQaService.DestinationQaResult result = qaService.ask(destinationId, request.question(), request.tripId());
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context missing");
        }
        return new DestinationQaResponse(result.answer(), result.cached(), result.promptTokens(), result.completionTokens());
    }
}
