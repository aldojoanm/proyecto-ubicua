package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.CreateNotificationRequest;
import com.solveria.backendservice.api.dto.NotificationResponse;
import com.solveria.backendservice.application.service.NotificationService;
import com.solveria.backendservice.domain.model.Notification;
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
@RequestMapping("/api/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create notification",
            description = "Creates a notification. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create notification payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateNotificationRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "channel": "EMAIL",
                                      "recipient": "demo@solveria.com",
                                      "message": "Your trip has been confirmed."
                                    }
                                    """
                    )
            )
    )
    public NotificationResponse create(@Valid @RequestBody CreateNotificationRequest request) {
        Notification notification = service.create(request.channel(), request.recipient(), request.message());
        return new NotificationResponse(notification.getId(), notification.getChannel(), notification.getRecipient(), notification.getMessage(), notification.getStatus());
    }

    @GetMapping
    @Operation(
            summary = "List notifications",
            description = "Lists notifications for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<NotificationResponse> list() {
        return service.listForTenant().stream()
                .map(n -> new NotificationResponse(n.getId(), n.getChannel(), n.getRecipient(), n.getMessage(), n.getStatus()))
                .toList();
    }
}
