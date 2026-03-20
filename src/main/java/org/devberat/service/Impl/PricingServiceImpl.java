package org.devberat.service.Impl;

import org.devberat.model.Flight;
import org.devberat.service.IPricingService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PricingServiceImpl implements IPricingService {

    @Override
    public BigDecimal calculatePrice(Flight flight) {
        if (flight.getBasePrice() == null) {
            return BigDecimal.ZERO;
        }

        double occupancyRate = 0.0;
        if (flight.getAircraft() != null && flight.getAircraft().getSeatCapacity() > 0) {
            occupancyRate = (double) flight.getCurrentOccupancy() / flight.getAircraft().getSeatCapacity();
        }

        BigDecimal currentPrice = flight.getBasePrice();

        // Occupancy factor for pricing
        if (occupancyRate > 0.9) {
            currentPrice = currentPrice.multiply(BigDecimal.valueOf(1.5));
        } else if (occupancyRate > 0.7) {
            currentPrice = currentPrice.multiply(BigDecimal.valueOf(1.3));
        } else if (occupancyRate > 0.5) {
            currentPrice = currentPrice.multiply(BigDecimal.valueOf(1.1));
        }

        // Last minute discount logic
        LocalDateTime now = LocalDateTime.now();
        if (flight.getDepartureTime() != null && 
            flight.getDepartureTime().isBefore(now.plusDays(1)) && 
            occupancyRate < 0.2) {
            currentPrice = currentPrice.multiply(BigDecimal.valueOf(0.8));
        }

        return currentPrice;
    }
}
