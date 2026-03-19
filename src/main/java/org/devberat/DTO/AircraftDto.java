package org.devberat.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class AircraftDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @NotBlank(message = "Model is required")
        private String model;
        @NotBlank(message = "Serial number is required")
        private String serialNumber;
        @Min(value = 1, message = "Seat capacity must be at least 1")
        private int seatCapacity;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info {
        private UUID id;
        private String model;
        private String serialNumber;
        private int seatCapacity;
        private boolean inService;
    }
}