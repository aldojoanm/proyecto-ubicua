package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.ActivityResponse;
import com.solveria.backendservice.api.dto.CreateActivityRequest;
import com.solveria.backendservice.application.service.ActivityService;
import com.solveria.backendservice.domain.model.Activity;
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
@RequestMapping("/api/activities")
@Tag(name = "Activities")
public class ActivityController {

    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create activity",
            description = "Creates an activity. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create activity payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateActivityRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "title": "City walk",
                                      "category": "tour",
                                      "location": "Paris",
                                      "startsAt": "2026-04-10T09:00:00",
                                      "endsAt": "2026-04-10T12:00:00"
                                    }
                                    """
                    )
            )
    )
    public ActivityResponse create(@Valid @RequestBody CreateActivityRequest request) {
        Activity activity = service.create(request.title(), request.category(), request.location(), request.startsAt(), request.endsAt());
        return new ActivityResponse(activity.getId(), activity.getTitle(), activity.getCategory(), activity.getLocation(), activity.getStartsAt(), activity.getEndsAt());
    }

    @GetMapping
    @Operation(
            summary = "List activities",
            description = "Lists activities for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<ActivityResponse> list() {
        return service.listForTenant().stream()
                .map(a -> new ActivityResponse(a.getId(), a.getTitle(), a.getCategory(), a.getLocation(), a.getStartsAt(), a.getEndsAt()))
                .toList();
    }
}
