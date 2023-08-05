package com.yuxin.messaging.request;

import com.yuxin.messaging.enums.Gender;
import lombok.Data;

@Data
public class RegisterUserRequest {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String repeatPassword;
    private String address;
    private Gender gender;
}
