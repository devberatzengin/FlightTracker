package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.DTO.UserDto;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.User;
import org.devberat.model.UserType;
import org.devberat.repository.IUserRepository;
import org.devberat.service.ISecurityService;
import org.devberat.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ISecurityService securityService;

    @Override
    public UserDto.CreateResponse saveUser(UserDto.CreateRequest request) {
        User saveUser = new User();
        BeanUtils.copyProperties(request, saveUser);
        saveUser.setPassword(passwordEncoder.encode(request.getPassword()));
        saveUser.setUserType(UserType.PASSENGER); // Default role
        saveUser.setActive(true);
        saveUser.setCreatedAt(new Date());
        saveUser.setUpdatedAt(new Date());

        User databaseResult = userRepository.save(saveUser);

        return UserDto.CreateResponse.builder()
                .id(databaseResult.getId())
                .firstName(databaseResult.getFirstName())
                .lastName(databaseResult.getLastName())
                .email(databaseResult.getEmail())
                .build();
    }

    @Override
    public UserDto.StatusResponse activateUser(UserDto.StatusChangeRequest request) {
        securityService.checkAuthority(UserType.ADMIN);

        User dbUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "User not found with email: " + request.getEmail())));

        dbUser.setActive(true);
        dbUser.setUpdatedAt(new Date());
        userRepository.save(dbUser);

        return UserDto.StatusResponse.builder()
                .id(dbUser.getId())
                .active(true)
                .build();
    }

    @Override
    public UserDto.StatusResponse inActiveUser(UserDto.StatusChangeRequest request) {
        securityService.checkAuthority(UserType.ADMIN);

        User dbUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "User not found with email: " + request.getEmail())));

        dbUser.setActive(false);
        dbUser.setUpdatedAt(new Date());
        userRepository.save(dbUser);

        return UserDto.StatusResponse.builder()
                .id(dbUser.getId())
                .active(false)
                .build();
    }

    @Override
    public UserDto.Info getMyProfile() {
        return convertToDto(securityService.getCurrentUser());
    }

    // Helpers

    private UserDto.Info convertToDto(User user) {
        return UserDto.Info.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType())
                .balance(user.getBalance())
                .miles(user.getMiles())
                .build();
    }
}