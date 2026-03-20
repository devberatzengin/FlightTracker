package org.devberat.service;

import org.devberat.model.Flight;
import java.math.BigDecimal;

public interface IPricingService {
    BigDecimal calculatePrice(Flight flight);
}
