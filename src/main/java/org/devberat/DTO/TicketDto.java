package org.devberat.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.devberat.model.TicketStatus;
import java.util.UUID;

public class TicketDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingRequest {
        private UUID flightId;
        private String seatNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private UUID id;
        private String flightNumber;
        private String passengerName;
        private String seatNumber;
        private TicketStatus status;
    }
}