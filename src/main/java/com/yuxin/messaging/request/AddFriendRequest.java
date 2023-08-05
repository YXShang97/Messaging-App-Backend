package com.yuxin.messaging.request;

import lombok.Data;

@Data
public class AddFriendRequest {
    int receiverUserId;
    String message;
}
