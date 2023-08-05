package com.yuxin.messaging.response;

import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListFriendResponse extends CommonResponse {
    List<User> friends;

    public ListFriendResponse() {
        super(Status.OK);
    }

    public ListFriendResponse(List<User> friends) {
        super(Status.OK);
        this.friends = friends;
    }
}
