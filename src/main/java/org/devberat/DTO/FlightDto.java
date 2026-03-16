package org.devberat.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.devberat.model.FlightStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class FlightDto {

    @Data
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Flight number is required")
        private String flightNumber;

        @NotBlank(message = "Departure airport IATA is required")
        private String departureAirportIata;

        @NotBlank(message = "Arrival airport IATA is required")
        private String arrivalAirportIata;

        @NotBlank(message = "Aircraft serial number is required")
        private String aircraftSerialNumber;

        private UUID captainId;

        @NotNull(message = "Departure time is required")
        private LocalDateTime departureTime;

        @NotNull(message = "Arrival time is required")
        private LocalDateTime arrivalTime;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private UUID id;
        private String flightNumber;
        private String departureAirportName;
        private String arrivalAirportName;
        private String aircraftModel;
        private String captainFullName;
        private FlightStatus status;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
    }
}