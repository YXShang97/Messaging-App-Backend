package com.yuxin.messaging.controller;


import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.User;
import com.yuxin.messaging.request.*;
import com.yuxin.messaging.response.CommonResponse;
import com.yuxin.messaging.response.UserLoginResponse;
import com.yuxin.messaging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public CommonResponse register(@RequestBody RegisterUserRequest registerUserRequest) throws MessagingServiceException {
        this.userService.register(registerUserRequest.getUsername(),
                registerUserRequest.getNickname(),
                registerUserRequest.getEmail(),
                registerUserRequest.getPassword(),
                registerUserRequest.getRepeatPassword(),
                registerUserRequest.getAddress(),
                registerUserRequest.getGender());

        return new CommonResponse(Status.OK);
    }

    @PostMapping("/activate")
    public CommonResponse activate(@RequestBody ActivateUserRequest activateUserRequest) throws MessagingServiceException {
        this.userService.activate(activateUserRequest.getIdentification(), activateUserRequest.getValidationCode());
        return new CommonResponse(Status.OK);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest userLoginRequest) throws MessagingServiceException {
        var loginToken = this.userService.login(userLoginRequest.getIdentification(), userLoginRequest.getPassword());
        return new UserLoginResponse(loginToken);
    }

    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Login-Token") String loginToken) throws MessagingServiceException {
        User user = this.userService.authenticate(loginToken);
        this.userService.logout(user.getId());
        return new CommonResponse(Status.OK);
    }

    @PostMapping("/forgetpassword")
    public CommonResponse forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) throws MessagingServiceException {
        this.userService.forgetPassword(forgetPasswordRequest.getEmail());
        return new CommonResponse(Status.OK);
    }

    @PostMapping("/resetpassword")
    public CommonResponse resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws MessagingServiceException {
        this.userService.resetPassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getValidationCode(), resetPasswordRequest.getPassword(), resetPasswordRequest.getRepeatPassword());
        return new CommonResponse(Status.OK);
    }
}
