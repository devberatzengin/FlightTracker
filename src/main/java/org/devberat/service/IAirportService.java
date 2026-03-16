package org.devberat.service;

import jakarta.validation.Valid;
import org.devberat.DTO.AirportDto;
import java.util.List;
import java.util.UUID;

public interface IAirportService {
    AirportDto.Info createAirport(AirportDto.Request request);
    List<AirportDto.Info> getAllAirports();
    AirportDto.Info getAirportById(UUID id);
    void deleteAirport(UUID id);
}