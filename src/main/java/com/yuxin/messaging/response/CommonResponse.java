package com.yuxin.messaging.response;

import com.yuxin.messaging.enums.Status;
import lombok.Data;

@Data
public class CommonResponse {
    private int code;
    private String message;

    public CommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(Status status) {
        this.code = status.getCode();
        this.message = status.getMessage();
    }
}
