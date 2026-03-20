package org.devberat.controller.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.model.RootEntity;
import org.devberat.service.IWalletService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/rest/api/wallet")
@RequiredArgsConstructor
public class WalletController extends RestBaseController {

    private final IWalletService walletService;

    @GetMapping("/balance")
    public RootEntity<BigDecimal> getBalance() {
        return ok(walletService.getBalance());
    }

    @PostMapping("/add-funds")
    public RootEntity<BigDecimal> addFunds(@RequestParam BigDecimal amount) {
        try {
            return ok(walletService.addFunds(amount));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }
}
