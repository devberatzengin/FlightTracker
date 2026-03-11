package org.devberat.service;

import org.devberat.DTO.*;

public interface IUserService {

    CreateUserResponse saveUser(CreateUserRequest request);

    InActiveUserResponse inActiveUser(InActiveUserRequest request);

    ActivateUserResponse activateUser(ActivateUserRequest request);
}