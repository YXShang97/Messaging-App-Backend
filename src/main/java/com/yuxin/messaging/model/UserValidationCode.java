package com.yuxin.messaging.model;

import lombok.Data;

@Data
public class UserValidationCode {  // activate, reset password
    private int id;
    private int userId;
    private String validationCode;  // 6 digits
}
