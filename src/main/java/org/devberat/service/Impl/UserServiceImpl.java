package org.devberat.service.Impl;

import org.devberat.DTO.UserDto;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.User;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto.CreateResponse saveUser(UserDto.CreateRequest request) {
        User saveUser = new User();
        BeanUtils.copyProperties(request, saveUser);
        saveUser.setPassword(passwordEncoder.encode(request.getPassword()));
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
        checkAdminAuthority();

        User dbUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, request.getEmail())));

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
        checkAdminAuthority();

        User dbUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, request.getEmail())));

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return UserDto.Info.builder()
                .id(currentUser.getId())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .email(currentUser.getEmail())
                .phoneNumber(currentUser.getPhoneNumber())
                .userType(currentUser.getUserType())
                .build();
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
                .build();
    }

    private void checkAdminAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Authentication required!"));
        }

        User currentUser = (User) authentication.getPrincipal();

        if (!org.devberat.model.UserType.ADMIN.equals(currentUser.getUserType())) {
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, "This operation requires ADMIN authority!"));
        }
    }
}