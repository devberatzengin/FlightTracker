package org.devberat.service;

import org.devberat.DTO.AircraftDto;
import java.util.List;
import java.util.UUID;

public interface IAircraftService {
    AircraftDto.Info createAircraft(AircraftDto.Request request);
    List<AircraftDto.Info> getAllAircrafts();
    AircraftDto.Info getAircraftById(UUID id);
    void deleteAircraft(UUID id);
}