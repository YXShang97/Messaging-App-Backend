package com.yuxin.messaging.request;

import lombok.Data;

@Data
public class ActivateUserRequest {
    private String identification;
    private String validationCode;
}
