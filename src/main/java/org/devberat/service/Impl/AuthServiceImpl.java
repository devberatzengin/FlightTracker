package org.devberat.service.Impl;

import org.devberat.DTO.AuthDto;
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
    public AuthDto.Response login(AuthDto.Request request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateToken(user);

        return AuthDto.Response.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}