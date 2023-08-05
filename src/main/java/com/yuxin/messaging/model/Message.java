package com.yuxin.messaging.model;

import com.yuxin.messaging.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.InsufficientResourcesException;
import java.awt.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private int id;
    private int senderUserId;
    private Integer groupChatId;
    private Integer receiverUserId;
    private Date sendTime;
    private MessageType messageType;
    private String content;
    private boolean valid;
}
