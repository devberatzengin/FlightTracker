package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.DTO.FlightDto;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.*;
import org.devberat.repository.IAircraftRepository;
import org.devberat.repository.IAirportRepository;
import org.devberat.repository.IFlightRepository;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IFlightService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements IFlightService {

    private final IFlightRepository flightRepository;
    private final IAirportRepository airportRepository;
    private final IAircraftRepository aircraftRepository;
    private final IUserRepository userRepository;

    @Override
    public FlightDto.Info assignCaptain(UUID flightId, UUID captainId) {
        checkFlightAuthority();

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found.")));

        User captain = userRepository.findById(captainId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Captain not found.")));

        if (captain.getUserType() != UserType.CAPTAIN) {
            throw new BaseException(new ErrorMessage(MessageType.USER_ROLE_ERROR, "The selected user is not a CAPTAIN."));
        }

        flight.setCaptain(captain);
        return convertToDto(flightRepository.save(flight));
    }


    @Override
    @Transactional
    public FlightDto.Info createFlight(FlightDto.CreateRequest request) {
        checkFlightAuthority(); // Check Auth

        // Check Time
        if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Arrival time cannot be earlier than departure time."));
        }

        // Check aircraft availability
        boolean isBusy = flightRepository.isAircraftBusy(
                request.getAircraftSerialNumber(),
                request.getDepartureTime(),
                request.getArrivalTime()
        );

        if (isBusy) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION,
                    "The aircraft is already scheduled for another flight during this time interval."));
        }

        Airport departureAirport = airportRepository.findByIataCode(request.getDepartureAirportIata())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Departure airport not found.")));

        Airport arrivalAirport = airportRepository.findByIataCode(request.getArrivalAirportIata())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Arrival airport not found.")));

        Aircraft aircraft = aircraftRepository.findBySerialNumber(request.getAircraftSerialNumber())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Aircraft not found.")));

        Flight flight = new Flight();

        // Update captain
        if (request.getCaptainId() != null) {
            User captain = userRepository.findById(request.getCaptainId())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Captain not found")));

            if (captain.getUserType() != UserType.CAPTAIN) {
                throw new BaseException(new ErrorMessage(MessageType.USER_ROLE_ERROR, "The selected user is not a CAPTAIN."));
            }
            flight.setCaptain(captain);
        } else {
            List<User> availableCaptains = userRepository.findAvailableCaptains(
                    request.getDepartureTime(),
                    request.getArrivalTime()
            );

            if (!availableCaptains.isEmpty()) {
                flight.setCaptain(availableCaptains.get(0));
            }
        }

        flight.setFlightNumber(request.getFlightNumber());
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setAircraft(aircraft);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setCurrentOccupancy(0);

        return convertToDto(flightRepository.save(flight));
    }
    @Override
    public List<FlightDto.Info> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FlightDto.Info getFlightById(UUID id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found with ID: " + id)));
        return convertToDto(flight);
    }

    @Override
    public FlightDto.Info updateFlightStatus(UUID flightId, FlightStatus newStatus) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found")));

        checkFlightAuthority();

        if (flight.getStatus() == FlightStatus.LANDED) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Cannot change status of a landed flight"));
        }

        flight.setStatus(newStatus);
        return convertToDto(flightRepository.save(flight));
    }

    @Override
    public void deleteFlight(UUID id) {
        checkFlightAuthority();
        if (!flightRepository.existsById(id)) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found with ID: " + id));
        }
        flightRepository.deleteById(id);
    }

    private void checkFlightAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!(currentUser.getUserType() == UserType.ADMIN || currentUser.getUserType() == UserType.TOWER)) {
            throw new AccessDeniedException("Access Denied: You must be an ADMIN or TOWER to perform this operation.");
        }
    }

    private FlightDto.Info convertToDto(Flight flight) {
        return FlightDto.Info.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .departureAirportName(flight.getDepartureAirport().getName())
                .arrivalAirportName(flight.getArrivalAirport().getName())
                .aircraftModel(flight.getAircraft().getModel())
                .captainFullName(flight.getCaptain() != null ?
                        flight.getCaptain().getFirstName() + " " + flight.getCaptain().getLastName() : "Not Assigned")
                .status(flight.getStatus())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .currentOccupancy(flight.getCurrentOccupancy())
                .build();
    }
}