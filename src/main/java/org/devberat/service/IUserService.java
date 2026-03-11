package org.devberat.service;

import org.devberat.DTO.CreateUserRequest;
import org.devberat.DTO.CreateUserResponse;

public interface IUserService {
    CreateUserResponse saveUser(CreateUserRequest request);
}