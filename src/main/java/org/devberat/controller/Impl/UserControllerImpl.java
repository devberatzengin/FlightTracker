package org.devberat.controller.Impl;

import jakarta.validation.Valid;
import org.devberat.DTO.CreateUserRequest;
import org.devberat.DTO.CreateUserResponse;
import org.devberat.DTO.InActiveUserRequest;
import org.devberat.DTO.InActiveUserResponse;
import org.devberat.model.RootEntity;
import org.devberat.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/user")
public class UserControllerImpl extends RestBaseController {
    @Autowired
    private IUserService userService;

    @PostMapping("/save")
    public RootEntity<CreateUserResponse> saveUser(@Valid @RequestBody CreateUserRequest request) {
        return ok(userService.saveUser(request));
    }

    @PostMapping("/deactivate")
    public RootEntity<InActiveUserResponse> deactivateUser(@Valid @RequestBody InActiveUserRequest request){
        return ok(userService.inActiveUser(request));
    }
}