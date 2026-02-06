package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.CreateReviewRequest;
import com.solveria.backendservice.api.dto.ReviewResponse;
import com.solveria.backendservice.application.service.ReviewService;
import com.solveria.backendservice.domain.model.Review;
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
@RequestMapping("/api/reviews")
@Tag(name = "Reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create review",
            description = "Creates a review. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create review payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateReviewRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "subject": "Great trip",
                                      "rating": 5,
                                      "comment": "Loved the itinerary and guides.",
                                      "targetType": "TRIP",
                                      "targetId": "1"
                                    }
                                    """
                    )
            )
    )
    public ReviewResponse create(@Valid @RequestBody CreateReviewRequest request) {
        Review review = service.create(request.subject(), request.rating(), request.comment(), request.targetType(), request.targetId());
        return new ReviewResponse(review.getId(), review.getSubject(), review.getRating(), review.getComment(), review.getTargetType(), review.getTargetId());
    }

    @GetMapping
    @Operation(
            summary = "List reviews",
            description = "Lists reviews for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<ReviewResponse> list() {
        return service.listForTenant().stream()
                .map(r -> new ReviewResponse(r.getId(), r.getSubject(), r.getRating(), r.getComment(), r.getTargetType(), r.getTargetId()))
                .toList();
    }
}
