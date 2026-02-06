package com.solveria.backendservice.api;

import com.solveria.backendservice.api.dto.BookingResponse;
import com.solveria.backendservice.api.dto.CreateBookingRequest;
import com.solveria.backendservice.application.service.BookingService;
import com.solveria.backendservice.domain.model.Booking;
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
@RequestMapping("/api/bookings")
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create booking",
            description = "Creates a booking. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Create booking payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateBookingRequest.class),
                    examples = @ExampleObject(
                            name = "Request Example",
                            value = """
                                    {
                                      "referenceCode": "BK-2026-0001",
                                      "tripId": 1,
                                      "totalAmount": 499.90,
                                      "currency": "USD"
                                    }
                                    """
                    )
            )
    )
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        Booking booking = service.create(request.referenceCode(), request.tripId(), request.totalAmount(), request.currency());
        return new BookingResponse(booking.getId(), booking.getReferenceCode(), booking.getTripId(), booking.getStatus(), booking.getTotalAmount(), booking.getCurrency());
    }

    @GetMapping
    @Operation(
            summary = "List bookings",
            description = "Lists bookings for the tenant. Requires JWT Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<BookingResponse> list() {
        return service.listForTenant().stream()
                .map(b -> new BookingResponse(b.getId(), b.getReferenceCode(), b.getTripId(), b.getStatus(), b.getTotalAmount(), b.getCurrency()))
                .toList();
    }
}
