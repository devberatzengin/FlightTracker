package org.devberat.service.Impl;

import org.devberat.DTO.*;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.User;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private UUID findUserByEmail(String userEmail){
        if (userEmail == null) {
            return null;
        }
        return userRepository.findByEmail(userEmail)
                .map(User::getId)
                .orElse(null);
    }

    @Override
    public CreateUserResponse saveUser(CreateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();
        User saveUser = new User();
        BeanUtils.copyProperties(request, saveUser);

        saveUser.setPassword(passwordEncoder.encode(request.getPassword()));

        saveUser.setActive(true);
        saveUser.setCreatedAt(new Date());
        saveUser.setUpdatedAt(new Date());

        User databaseResult = userRepository.save(saveUser);

        if (databaseResult.getId() != null) {
            BeanUtils.copyProperties(databaseResult, response);
        }
        return response;
    }

    @Override
    public ActivateUserResponse activateUser(ActivateUserRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, request.getEmail()));
        }

        User dbUser = optionalUser.get();
        dbUser.setActive(true);
        dbUser.setUpdatedAt(new Date());

        userRepository.save(dbUser);

        ActivateUserResponse response = new ActivateUserResponse();
        BeanUtils.copyProperties(dbUser, response);

        return response;
    }

    @Override
    public InActiveUserResponse inActiveUser(InActiveUserRequest request){

        //Find User
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        // İf not found throw exception
        if (optionalUser.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, request.getEmail()));
        }


        User dbUser = optionalUser.get();
        dbUser.setActive(false);
        dbUser.setUpdatedAt(new Date());
        userRepository.save(dbUser);

        InActiveUserResponse response = new InActiveUserResponse();
        BeanUtils.copyProperties(dbUser, response);

        return response;
    }
}