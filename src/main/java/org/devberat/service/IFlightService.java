package org.devberat.service;

import org.devberat.DTO.FlightDto;
import org.devberat.model.FlightStatus;

import java.util.List;
import java.util.UUID;

public interface IFlightService {
    FlightDto.Info createFlight(FlightDto.CreateRequest request);
    List<FlightDto.Info> getAllFlights();
    FlightDto.Info assignCaptain(UUID flightId, UUID captainId);
    FlightDto.Info updateFlightStatus(UUID flightId, FlightStatus newStatus);
    FlightDto.Info getFlightById(UUID id);
    void deleteFlight(UUID id);
}