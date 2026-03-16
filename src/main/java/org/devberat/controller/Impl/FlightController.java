package org.devberat.controller.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devberat.DTO.FlightDto;
import org.devberat.model.FlightStatus;
import org.devberat.model.RootEntity;
import org.devberat.service.IFlightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/api/flight")
@RequiredArgsConstructor
public class FlightController extends RestBaseController {

    private final IFlightService flightService;

    @PostMapping("/create")
    public RootEntity<FlightDto.Info> createFlight(@Valid @RequestBody FlightDto.CreateRequest request) {
        return ok(flightService.createFlight(request));
    }

    @PutMapping("/{flightId}/assign-captain/{captainId}")
    public RootEntity<FlightDto.Info> assignCaptain(@PathVariable UUID flightId, @PathVariable UUID captainId) {
        return ok(flightService.assignCaptain(flightId, captainId));
    }

    @PutMapping("/update-status/{id}")
    public RootEntity<FlightDto.Info> updateStatus(@PathVariable UUID id, @RequestParam FlightStatus status) {
        return ok(flightService.updateFlightStatus(id, status));
    }

    @GetMapping("/list")
    public RootEntity<List<FlightDto.Info>> getAllFlights() {
        return ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public RootEntity<FlightDto.Info> getFlightById(@PathVariable UUID id) {
        return ok(flightService.getFlightById(id));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<Void> deleteFlight(@PathVariable UUID id) {
        flightService.deleteFlight(id);
        return ok(null);
    }
}