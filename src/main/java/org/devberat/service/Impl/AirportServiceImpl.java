package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.DTO.AirportDto;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.Airport;
import org.devberat.model.User;
import org.devberat.model.UserType;
import org.devberat.repository.IAirportRepository;
import org.devberat.service.IAirportService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements IAirportService {
    private final IAirportRepository airportRepository;

    @Override
    public AirportDto.Info createAirport(AirportDto.Request request) {
        checkAdminAuthority();
        if (airportRepository.findByIataCode(request.getIataCode()).isPresent()) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Airport with this IATA code already exists"));
        }
        Airport airport = new Airport();
        airport.setIataCode(request.getIataCode().toUpperCase());
        airport.setName(request.getName());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());
        airport.setActive(true);
        return convertToDto(airportRepository.save(airport));
    }

    @Override
    public List<AirportDto.Info> getAllAirports() {
        return airportRepository.findAll().stream().map(this::convertToDto).toList();
    }

    @Override
    public AirportDto.Info getAirportById(UUID id) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Airport not found with ID: " + id)));
        return convertToDto(airport);
    }

    @Override
    public void deleteAirport(UUID id) {
        checkAdminAuthority();
        if (!airportRepository.existsById(id)) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Airport not found with ID: " + id));
        }
        airportRepository.deleteById(id);
    }

    private void checkAdminAuthority() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getUserType() != UserType.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can perform this operation");
        }
    }

    private AirportDto.Info convertToDto(Airport airport) {
        return AirportDto.Info.builder()
                .id(airport.getId())
                .iataCode(airport.getIataCode())
                .name(airport.getName())
                .city(airport.getCity())
                .country(airport.getCountry())
                .isActive(airport.isActive())
                .build();
    }
}