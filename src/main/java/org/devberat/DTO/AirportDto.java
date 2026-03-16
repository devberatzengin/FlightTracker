package org.devberat.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class AirportDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "IATA code is required")
        @Size(min = 3, max = 3, message = "IATA code must be 3 characters")
        private String iataCode;
        @NotBlank(message = "Airport name is required")
        private String name;
        @NotBlank(message = "City is required")
        private String city;
        @NotBlank(message = "Country is required")
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private UUID id;
        private String iataCode;
        private String name;
        private String city;
        private String country;
        private boolean isActive;
    }
}