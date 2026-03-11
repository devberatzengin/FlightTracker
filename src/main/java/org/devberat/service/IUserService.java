package org.devberat.service;

import org.devberat.DTO.CreateUserRequest;
import org.devberat.DTO.CreateUserResponse;
import org.devberat.DTO.InActiveUserRequest;
import org.devberat.DTO.InActiveUserResponse;

public interface IUserService {

    CreateUserResponse saveUser(CreateUserRequest request);

    InActiveUserResponse inActiveUser(InActiveUserRequest request);
}