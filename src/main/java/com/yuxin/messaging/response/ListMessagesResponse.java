package com.yuxin.messaging.response;

import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.model.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListMessagesResponse extends CommonResponse {
    List<Message> messages;

    public ListMessagesResponse() {
        super(Status.OK);
    }

    public ListMessagesResponse(List<Message> messages) {
        super(Status.OK);
        this.messages = messages;
    }
}

