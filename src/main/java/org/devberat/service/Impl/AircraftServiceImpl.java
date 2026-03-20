package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.DTO.AircraftDto;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.Aircraft;
import org.devberat.model.UserType;
import org.devberat.repository.IAircraftRepository;
import org.devberat.service.IAircraftService;
import org.devberat.service.ISecurityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements  IAircraftService{
    private final IAircraftRepository aircraftRepository;
    private final ISecurityService securityService;

    @Override
    public AircraftDto.Info createAircraft(AircraftDto.Request request) {
        securityService.checkAuthority(UserType.ADMIN);
        if (aircraftRepository.findBySerialNumber(request.getSerialNumber()).isPresent()) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Aircraft with this serial number already exists"));
        }

        Aircraft aircraft = new Aircraft();
        aircraft.setModel(request.getModel());
        aircraft.setSerialNumber(request.getSerialNumber());
        aircraft.setSeatCapacity(request.getSeatCapacity());
        aircraft.setInService(true);
        return convertToDto(aircraftRepository.save(aircraft));
    }

    @Override
    public List<AircraftDto.Info> getAllAircrafts() {
        return aircraftRepository.findAll().stream().map(this::convertToDto).toList();
    }

    @Override
    public AircraftDto.Info getAircraftById(UUID id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Aircraft not found with ID: " + id)));
        return convertToDto(aircraft);
    }

    @Override
    public void deleteAircraft(UUID id) {
        securityService.checkAuthority(UserType.ADMIN);
        if (!aircraftRepository.existsById(id)) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Aircraft not found with ID: " + id));
        }
        aircraftRepository.deleteById(id);
    }

    // Authority checks moved to securityService.

    private AircraftDto.Info convertToDto(Aircraft aircraft) {
        return AircraftDto.Info.builder()
                .id(aircraft.getId())
                .model(aircraft.getModel())
                .serialNumber(aircraft.getSerialNumber())
                .seatCapacity(aircraft.getSeatCapacity())
                .inService(aircraft.isInService())
                .build();
    }
}