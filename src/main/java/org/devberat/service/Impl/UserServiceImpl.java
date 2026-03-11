package org.devberat.service.Impl;

import org.devberat.DTO.CreateUserRequest;
import org.devberat.DTO.CreateUserResponse;
import org.devberat.model.User;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public CreateUserResponse saveUser(CreateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();
        User saveUser = new User();
        BeanUtils.copyProperties(request, saveUser);
        saveUser.setActive(true);
        saveUser.setCreatedAt(new Date());
        saveUser.setUpdatedAt(new Date());

        User databaseResult = userRepository.save(saveUser);

        if (databaseResult.getId() != null) {
            BeanUtils.copyProperties(databaseResult, response);
        }
        return response;
    }
}