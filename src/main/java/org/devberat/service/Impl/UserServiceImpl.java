package org.devberat.service.Impl;

import org.devberat.DTO.*;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.User;
import org.devberat.model.UserType;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType().name()) // Enum'ı String'e çevirdik
                .build();
    }

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
        // 1. ADIM: Önce masaya yumruğu vuruyoruz: Admin misin?
        checkAdminAuthority();

        // 2. ADIM: İşlemlere devam
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
        checkAdminAuthority();

        // 2. ADIM: Kullanıcıyı bul
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, request.getEmail()));
        }

        User dbUser = optionalUser.get();
        dbUser.setActive(false); // Pasif yapma işlemi
        dbUser.setUpdatedAt(new Date());
        userRepository.save(dbUser);

        InActiveUserResponse response = new InActiveUserResponse();
        BeanUtils.copyProperties(dbUser, response);

        return response;
    }

    private void checkAdminAuthority() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Giriş yapmanız gerekiyor!"));
        }

        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getUserType().equals(UserType.ADMIN)) {
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, "Bu işlem için ADMIN yetkisi gerekiyor!"));
        }
    }

    @Override
    public UserDto getMyProfile() {
        // SecurityContext içinden o anki giriş yapmış kullanıcıyı alıyoruz
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return convertToDto(currentUser);

    }
}