package com.yuxin.messaging.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String identification;  // username or email
    private String password;
}
