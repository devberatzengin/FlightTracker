package org.devberat.service.Impl;

import org.devberat.service.IPnrService;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class PnrServiceImpl implements IPnrService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PNR_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generatePnr() {
        StringBuilder pnr = new StringBuilder(PNR_LENGTH);
        for (int i = 0; i < PNR_LENGTH; i++) {
            pnr.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return pnr.toString();
    }
}
