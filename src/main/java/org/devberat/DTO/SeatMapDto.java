package org.devberat.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SeatMapDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatInfo {
        private String seatNumber; // 1A, 1B vb.
        private boolean isAvailable;
    }
}