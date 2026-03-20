package org.devberat.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.devberat.model.TicketStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class TicketDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingRequest {
        private UUID flightId;
        private String seatNumber;
        private boolean useWallet;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private UUID id;
        private BigDecimal price;
        private String flightNumber;
        private String passengerName;
        private String departureCity;
        private String arrivalCity;
        private String departureTime;
        private String arrivalTime;
        private String seatNumber;
        private String pnrCode;
        
        @com.fasterxml.jackson.annotation.JsonProperty("isCheckedIn")
        private boolean isCheckedIn;
        
        private TicketStatus status;
    }
}