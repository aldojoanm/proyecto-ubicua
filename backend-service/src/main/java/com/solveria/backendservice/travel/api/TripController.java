package com.solveria.backendservice.travel.api;

import com.solveria.backendservice.travel.api.dto.CreateTripRequest;
import com.solveria.backendservice.travel.api.dto.GenerateItineraryRequest;
import com.solveria.backendservice.travel.api.dto.ItineraryResponse;
import com.solveria.backendservice.travel.api.dto.TripResponse;
import com.solveria.backendservice.travel.application.ItineraryService;
import com.solveria.backendservice.travel.application.TripService;
import com.solveria.backendservice.travel.domain.model.Trip;
import com.solveria.backendservice.travel.infrastructure.mongo.ItineraryDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;
    private final ItineraryService itineraryService;

    public TripController(TripService tripService, ItineraryService itineraryService) {
        this.tripService = tripService;
        this.itineraryService = itineraryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create trip",
            description = "Creates a trip for the authenticated tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create trip payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateTripRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "title": "Family trip",
                                      "origin": "SCL",
                                      "destination": "LIM",
                                      "startDate": "2026-04-10",
                                      "endDate": "2026-04-15",
                                      "travelersCount": 3,
                                      "budget": 1200.00
                                    }
                                    """
                    )
            )
    )
    public TripResponse create(@Valid @RequestBody CreateTripRequest request) {
        Trip trip = tripService.create(
                request.title(),
                request.origin(),
                request.destination(),
                request.startDate(),
                request.endDate(),
                request.travelersCount(),
                request.budget()
        );
        return toResponse(trip);
    }

    @GetMapping
    @Operation(
            summary = "List trips",
            description = "Lists trips for the authenticated tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<TripResponse> list() {
        return tripService.listForTenant().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{tripId}")
    @Operation(
            summary = "Get trip",
            description = "Gets a trip for the authenticated tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public TripResponse get(@PathVariable Long tripId) {
        Trip trip = tripService.getForTenant(tripId);
        if (trip == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        return toResponse(trip);
    }

    @PostMapping("/{tripId}/itinerary/generate")
    @Operation(
            summary = "Generate itinerary",
            description = "Calls AI Service to generate itinerary. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Optional preferences for itinerary generation",
            required = false,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GenerateItineraryRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "preferences": "family-friendly activities"
                                    }
                                    """
                    )
            )
    )
    public ItineraryResponse generateItinerary(@PathVariable Long tripId, @RequestBody GenerateItineraryRequest request) {
        ItineraryDocument doc = itineraryService.generate(tripId, request != null ? request.preferences() : null);
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        return new ItineraryResponse(doc.getTripId(), doc.getVersion(), doc.getContent(), doc.getModel(), doc.getTokensUsed(), doc.getGeneratedAt());
    }

    @GetMapping("/{tripId}/itinerary")
    @Operation(
            summary = "Get latest itinerary",
            description = "Returns the latest itinerary for a trip. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ItineraryResponse getLatestItinerary(@PathVariable Long tripId) {
        ItineraryDocument doc = itineraryService.getLatest(tripId);
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Itinerary not found");
        }
        return new ItineraryResponse(doc.getTripId(), doc.getVersion(), doc.getContent(), doc.getModel(), doc.getTokensUsed(), doc.getGeneratedAt());
    }

    private TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getTravelersCount(),
                trip.getStatus(),
                trip.getBudget()
        );
    }
}
