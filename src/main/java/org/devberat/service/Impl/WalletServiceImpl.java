package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.model.User;
import org.devberat.repository.IUserRepository;
import org.devberat.service.ISecurityService;
import org.devberat.service.IWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements IWalletService {

    private final IUserRepository userRepository;
    private final ISecurityService securityService;
    private final NotificationService notificationService;

    @Override
    public BigDecimal getBalance() {
        return securityService.getCurrentUser().getBalance();
    }

    @Override
    @Transactional
    public BigDecimal addFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        User user = securityService.getCurrentUser();
        user.refundBalance(amount); // Reuse refundBalance for adding funds
        userRepository.save(user);

        notificationService.sendNotification(user, 
            "Successfully added $" + amount + " to your SkyWallet!", 
            "WALLET_UPDATE");

        return user.getBalance();
    }
}
