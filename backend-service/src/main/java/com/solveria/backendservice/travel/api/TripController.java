package com.solveria.backendservice.travel.api;

import com.solveria.backendservice.travel.api.dto.CreateTripRequest;
import com.solveria.backendservice.travel.api.dto.GenerateItineraryRequest;
import com.solveria.backendservice.travel.api.dto.ItineraryResponse;
import com.solveria.backendservice.travel.api.dto.TripResponse;
import com.solveria.backendservice.travel.application.ItineraryService;
import com.solveria.backendservice.travel.application.TripService;
import com.solveria.backendservice.travel.domain.model.Trip;
import com.solveria.backendservice.travel.infrastructure.mongo.ItineraryDocument;
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
    public TripResponse create(@Valid @RequestBody CreateTripRequest request) {
        Trip trip = tripService.create(
                request.title(),
                request.origin(),
                request.destination(),
                request.startDate(),
                request.endDate(),
                request.travelersCount()
        );
        return toResponse(trip);
    }

    @GetMapping
    public List<TripResponse> list() {
        return tripService.listForTenant().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{tripId}")
    public TripResponse get(@PathVariable Long tripId) {
        Trip trip = tripService.getForTenant(tripId);
        if (trip == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        return toResponse(trip);
    }

    @PostMapping("/{tripId}/itinerary/generate")
    public ItineraryResponse generateItinerary(@PathVariable Long tripId, @RequestBody GenerateItineraryRequest request) {
        ItineraryDocument doc = itineraryService.generate(tripId, request != null ? request.preferences() : null);
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        return new ItineraryResponse(doc.getTripId(), doc.getVersion(), doc.getContent(), doc.getModel(), doc.getTokensUsed(), doc.getGeneratedAt());
    }

    @GetMapping("/{tripId}/itinerary")
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
                trip.getStatus()
        );
    }
}
