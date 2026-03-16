package org.devberat.controller.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devberat.DTO.AircraftDto;
import org.devberat.model.RootEntity;
import org.devberat.service.IAircraftService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/api/aircraft")
@RequiredArgsConstructor
public class AircraftController extends RestBaseController {

    private final IAircraftService aircraftService;

    @PostMapping("/create")
    public RootEntity<AircraftDto.Info> createAircraft(@Valid @RequestBody AircraftDto.Request request) {
        return ok(aircraftService.createAircraft(request));
    }

    @GetMapping("/list")
    public RootEntity<List<AircraftDto.Info>> getAllAircrafts() {
        return ok(aircraftService.getAllAircrafts());
    }

    @GetMapping("/{id}")
    public RootEntity<AircraftDto.Info> getAircraftById(@PathVariable UUID id) {
        return ok(aircraftService.getAircraftById(id));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<Void> deleteAircraft(@PathVariable UUID id) {
        aircraftService.deleteAircraft(id);
        return ok(null);
    }
}