package com.solveria.ai.api.controller;

import com.solveria.ai.api.request.RagQaRequest;
import com.solveria.ai.api.request.RagSeedRequest;
import com.solveria.ai.application.seed.RagSeedStore;
import com.solveria.ai.api.response.RagQaResponse;
import com.solveria.ai.application.dto.RagQaCommandDto;
import com.solveria.ai.application.dto.RagQaResultDto;
import com.solveria.ai.application.port.in.RagQaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for RAG QA.
 */
@RestController
@RequestMapping("/api/v1/ai")
public class RagQaController {

    private final RagQaUseCase ragQaUseCase;

    public RagQaController(RagQaUseCase ragQaUseCase) {
        this.ragQaUseCase = ragQaUseCase;
    }

    @PostMapping("/rag/qa")
    @Operation(
            summary = "RAG question answering",
            description = "Answers questions using RAG. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "RAG question payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RagQaRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "question": "Which neighborhoods are best for families?",
                                      "namespace": "tenant:tenant-001:destination:paris"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<RagQaResponse> qa(@Valid @RequestBody RagQaRequest request) {
        var command = new RagQaCommandDto(request.question(), request.namespace());
        RagQaResultDto result = ragQaUseCase.ask(command);
        return ResponseEntity.ok(new RagQaResponse(
                result.answer(),
                result.promptTokens(),
                result.completionTokens()
        ));
    }

    @PostMapping("/rag/seed")
    @Operation(
            summary = "Seed RAG context (dev)",
            description = "Stores sample context for a namespace in the dev stub store",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "RAG seed payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RagSeedRequest.class),
                    examples = @ExampleObject(
                            name = "Seed Example",
                            value = """
                                    {
                                      "namespace": "tenant:tenant-001:destination:paris",
                                      "content": "Paris highlights: Eiffel Tower, Louvre Museum, Montmartre, Seine cruises."
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<Void> seed(@Valid @RequestBody RagSeedRequest request) {
        RagSeedStore.seed(request.namespace(), request.content());
        return ResponseEntity.ok().build();
    }
}
