package com.yuxin.messaging.request;

import lombok.Data;

@Data
public class AddGroupChatMemberRequest {
    int groupChatId;
    int guestId;
}
