package com.yuxin.messaging.request;

import lombok.Data;

@Data
public class UpdateGroupChatRequest {
    int id;
    String name;
    String description;
}
