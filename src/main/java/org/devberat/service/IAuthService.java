package org.devberat.service;

import org.devberat.DTO.AuthRequest;
import org.devberat.DTO.AuthResponse;

public interface IAuthService {
    AuthResponse login(AuthRequest request);
}
