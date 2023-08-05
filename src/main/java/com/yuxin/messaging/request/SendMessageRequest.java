package com.yuxin.messaging.request;

import com.yuxin.messaging.enums.MessageType;
import lombok.Data;

@Data
public class SendMessageRequest {
    private MessageType messageType;
    private int receiverUserId;
    private int groupChatId;
    private String content;
}
