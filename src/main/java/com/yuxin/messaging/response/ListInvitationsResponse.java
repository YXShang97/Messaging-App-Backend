package com.yuxin.messaging.response;

import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.model.FriendInvitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ListInvitationsResponse extends CommonResponse {
    List<FriendInvitation> friendInvitations;

    public ListInvitationsResponse() {
        super(Status.OK);
    }

    public ListInvitationsResponse(List<FriendInvitation> friendInvitations) {
        super(Status.OK);
        this.friendInvitations = friendInvitations;
    }
}
