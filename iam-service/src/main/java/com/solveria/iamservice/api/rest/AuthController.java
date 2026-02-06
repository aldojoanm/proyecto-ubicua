package com.solveria.iamservice.api.rest;

import com.solveria.iamservice.api.rest.dto.TokenRequest;
import com.solveria.iamservice.api.rest.dto.TokenResponse;
import com.solveria.iamservice.application.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication and token issuance")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    @Operation(
            summary = "Issue JWT token (local)",
            description = "Dev-only endpoint that issues a local JWT token. Use the token in Swagger Authorize."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Token request payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TokenRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "userId": 1,
                                      "username": "demo",
                                      "email": "demo@solveria.com",
                                      "tenantId": "tenant-001",
                                      "scopes": ["ai.complete", "backend.read"]
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Token issued",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TokenResponse.class),
                    examples = @ExampleObject(
                            name = "Token Response",
                            value = """
                                    {
                                      "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "tokenType": "Bearer",
                                      "expiresAt": "2026-02-05T22:45:00Z",
                                      "scopes": ["ai.complete", "backend.read"]
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<TokenResponse> token(@Valid @RequestBody TokenRequest request) {
        var result = tokenService.issueToken(
                request.userId(),
                request.username(),
                request.tenantId(),
                request.scopes()
        );
        return ResponseEntity.ok(new TokenResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresAt(),
                result.scopes()
        ));
    }
}
