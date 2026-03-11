package org.devberat.controller.Impl;

import org.devberat.DTO.AuthRequest;
import org.devberat.DTO.AuthResponse;
import org.devberat.DTO.CreateUserRequest;
import org.devberat.DTO.CreateUserResponse;
import org.devberat.model.RootEntity;
import org.devberat.service.IAuthService;
import org.devberat.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/api/auth")
@RequiredArgsConstructor
public class AuthController extends RestBaseController {

    private final IAuthService authService;
    private final IUserService userService;

    @PostMapping("/register")
    public RootEntity<CreateUserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ok(userService.saveUser(request));
    }

    @PostMapping("/login")
    public RootEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ok(authService.login(request));
    }
}