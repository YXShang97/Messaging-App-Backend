package com.yuxin.messaging.model;

import lombok.Data;

@Data
public class GroupChatMember {
    private int id;
    private int groupChatId;
    private int memberUserId;
}
