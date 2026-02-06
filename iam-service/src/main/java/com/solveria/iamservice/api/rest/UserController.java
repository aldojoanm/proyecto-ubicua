package com.solveria.iamservice.api.rest;

import com.solveria.core.iam.domain.model.User;
import com.solveria.iamservice.api.rest.dto.UserCreateRequest;
import com.solveria.iamservice.api.rest.dto.UserResponse;
import com.solveria.iamservice.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create user")
    @ApiResponse(
            responseCode = "201",
            description = "User created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject(
                            name = "User Response",
                            value = """
                                    {
                                      "id": 1,
                                      "username": "demo",
                                      "email": "demo@solveria.com",
                                      "active": true
                                    }
                                    """
                    )
            )
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create user payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserCreateRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "username": "demo",
                                      "email": "demo@solveria.com",
                                      "tenantId": "tenant-001"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.create(request.username(), request.email(), request.tenantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @GetMapping
    @Operation(summary = "List users")
    public List<UserResponse> list() {
        return userService.list().stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive()
        );
    }
}
