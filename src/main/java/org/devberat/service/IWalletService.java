package org.devberat.service;

import java.math.BigDecimal;

public interface IWalletService {
    BigDecimal getBalance();
    BigDecimal addFunds(BigDecimal amount);
}
