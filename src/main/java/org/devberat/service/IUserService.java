package org.devberat.service;

import org.devberat.DTO.UserDto;

public interface IUserService {

    UserDto.CreateResponse saveUser(UserDto.CreateRequest request);

    UserDto.StatusResponse inActiveUser(UserDto.StatusChangeRequest request);

    UserDto.StatusResponse activateUser(UserDto.StatusChangeRequest request);

    UserDto.Info getMyProfile();
}