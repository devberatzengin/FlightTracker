package org.devberat.controller.Impl;

import jakarta.validation.Valid;
import org.devberat.DTO.*;
import org.devberat.model.RootEntity;
import org.devberat.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/user")
public class UserControllerImpl extends RestBaseController {
    @Autowired
    private IUserService userService;

    // Post's
    @PostMapping("/save")
    public RootEntity<CreateUserResponse> saveUser(@Valid @RequestBody CreateUserRequest request) {
        return ok(userService.saveUser(request));
    }

    @PostMapping("/activate")
    public RootEntity<ActivateUserResponse> activateUser(@Valid @RequestBody ActivateUserRequest request) {
        return ok(userService.activateUser(request));
    }

    @PostMapping("/deactivate")
    public RootEntity<InActiveUserResponse> deactivateUser(@Valid @RequestBody InActiveUserRequest request){
        return ok(userService.inActiveUser(request));
    }


    // Get's
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }
}