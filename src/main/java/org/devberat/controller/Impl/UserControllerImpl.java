package org.devberat.controller.Impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.devberat.DTO.UserDto;
import org.devberat.model.RootEntity;
import org.devberat.service.IUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/user")
@RequiredArgsConstructor
public class UserControllerImpl extends RestBaseController {

    private final IUserService userService;

    // Post Methods
    @PostMapping("/save")
    public RootEntity<UserDto.CreateResponse> saveUser(@Valid @RequestBody UserDto.CreateRequest request) {
        return ok(userService.saveUser(request));
    }

    @PostMapping("/activate")
    public RootEntity<UserDto.StatusResponse> activateUser(@Valid @RequestBody UserDto.StatusChangeRequest request) {
        return ok(userService.activateUser(request));
    }

    @PostMapping("/deactivate")
    public RootEntity<UserDto.StatusResponse> deactivateUser(@Valid @RequestBody UserDto.StatusChangeRequest request){
        return ok(userService.inActiveUser(request));
    }

    // Get Methods
    @GetMapping("/me")
    public RootEntity<UserDto.Info> getMyProfile() {
        return ok(userService.getMyProfile());
    }
}