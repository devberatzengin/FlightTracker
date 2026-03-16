package org.devberat.controller.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devberat.DTO.AirportDto;
import org.devberat.model.RootEntity;
import org.devberat.service.IAirportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/api/airport")
@RequiredArgsConstructor
public class AirportController extends RestBaseController {
    private final IAirportService airportService;

    @PostMapping("/create")
    public RootEntity<AirportDto.Info> create(@Valid @RequestBody AirportDto.Request request) {
        return ok(airportService.createAirport(request));
    }

    @GetMapping("/list")
    public RootEntity<List<AirportDto.Info>> list() {
        return ok(airportService.getAllAirports());
    }

    @GetMapping("/{id}")
    public RootEntity<AirportDto.Info> getAirportById(@PathVariable UUID id) {
        return ok(airportService.getAirportById(id));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<Void> deleteAirport(@PathVariable UUID id) {
        airportService.deleteAirport(id);
        return ok(null);
    }
}