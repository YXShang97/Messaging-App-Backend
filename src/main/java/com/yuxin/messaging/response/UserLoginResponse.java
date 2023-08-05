package com.yuxin.messaging.response;

import com.yuxin.messaging.enums.Status;

public class UserLoginResponse extends CommonResponse {
    String loginToken;

    public UserLoginResponse(String loginToken) {
        super(Status.OK);
        this.loginToken = loginToken;
    }

    public String getLoginToken() {
        return loginToken;
    }
}
