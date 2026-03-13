package org.devberat.service;

import org.devberat.DTO.AuthDto;

public interface IAuthService {
    AuthDto.Response login(AuthDto.Request request);
}