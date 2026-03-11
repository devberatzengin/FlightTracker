package org.devberat.service.Impl;

import org.devberat.DTO.AuthRequest;
import org.devberat.DTO.AuthResponse;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IAuthService;
import org.devberat.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(AuthRequest request) {
        //Email ve Password Check
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If Check successful get user
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        // Create token
        var token = jwtService.generateToken(user);

        // return token
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}