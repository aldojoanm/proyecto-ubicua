package com.solveria.backendservice.travel.api;

import com.solveria.backendservice.travel.api.dto.DestinationQaRequest;
import com.solveria.backendservice.travel.api.dto.DestinationQaResponse;
import com.solveria.backendservice.travel.application.DestinationQaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(
            summary = "Ask AI about a destination",
            description = "Uses AI Service (RAG QA) and requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Question payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DestinationQaRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "question": "What are the must-see spots?",
                                      "tripId": 1
                                    }
                                    """
                    )
            )
    )
    public DestinationQaResponse ask(@PathVariable String destinationId, @Valid @RequestBody DestinationQaRequest request) {
        DestinationQaService.DestinationQaResult result = qaService.ask(destinationId, request.question(), request.tripId());
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context missing");
        }
        return new DestinationQaResponse(result.answer(), result.cached(), result.promptTokens(), result.completionTokens());
    }
}
