package com.yuxin.messaging.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String validationCode;
    private String password;
    private String repeatPassword;
}
