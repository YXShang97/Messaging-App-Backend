package com.yuxin.messaging.enums;

import org.springframework.http.HttpStatus;

public enum Status {
    OK(1000, "SUCCESS", HttpStatus.OK),
    PASSWORD_NOT_MATCH(1001, "Password are not matched.", HttpStatus.BAD_REQUEST),
    EMPTY_USERNAME(1002, "Username is empty.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS_ALREADY(1003, "Email already exists.", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTS_ALREADY(1004, "Username already exists.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1005, "User doesn't exist.", HttpStatus.BAD_REQUEST),
    VALIDATION_CODE_NOT_MATCH(1006, "Validation code not match.", HttpStatus.BAD_REQUEST),
    USERNAME_AND_PASSWORD_NOT_MATCH(1007, "Username and password are not matched.", HttpStatus.FORBIDDEN),
    INVALID_USER(1008, "User hasn't been activated.", HttpStatus.FORBIDDEN),
    EXPIRED_LOGIN_TOKEN (1009, "Login token has expired.", HttpStatus.FORBIDDEN),
    RECEIVER_NOT_EXISTS(1010, "Receiver doesn't exist.", HttpStatus.BAD_REQUEST),
    ALREADY_FRIENDS(1011, "Already friends.", HttpStatus.BAD_REQUEST),
    INVITATION_TOO_FREQUENT(1012, "Invitation too frequently.", HttpStatus.BAD_REQUEST),
    INVITATION_IS_PENDING(1013, "Invitation is pending.", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_EXISTS(1014, "Invitation doesn't exist.", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_FOR_YOU(1015, "Invitation is not for you.", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_PENDING(1016, "Invitation is not pending.", HttpStatus.BAD_REQUEST),
    GROUP_CHAT_NOT_EXISTS(1017, "Group chat doesn't exist.", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_GROUP_CHAT(1018, "User isn't in the group chat.", HttpStatus.BAD_REQUEST),
    GUEST_ALREADY_IN_GROUP_CHAT(1019, "Guest is already in the group chat.", HttpStatus.BAD_REQUEST),
    GUEST_NOT_YOUR_FRIEND(1020, "Guest is not your friend.", HttpStatus.BAD_REQUEST),
    USER_NOT_CREATOR(1021, "User is not the creator of the group chat.", HttpStatus.BAD_REQUEST),
    SENDER_NOT_IN_GROUP_CHAT(1022, "Sender is not in the group chat.", HttpStatus.BAD_REQUEST),
    SENDER_AND_RECEIVER_NOT_FRIEND(1023, "Sender and receiver are not friends.", HttpStatus.BAD_REQUEST),
    RECEIVER_NOT_FOUND(1024, "Receiver not found.", HttpStatus.BAD_REQUEST),
    MESSAGE_NOT_FOUND(1025, "Message not found.", HttpStatus.BAD_REQUEST),
    UNKNOWN_EXCEPTION(9999, "Unknown exception.", HttpStatus.INTERNAL_SERVER_ERROR);

    private int code;
    private String message;
    private HttpStatus httpStatus;

    Status(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
