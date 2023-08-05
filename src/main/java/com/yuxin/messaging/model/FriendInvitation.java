package com.yuxin.messaging.model;

import java.time.Duration;
import java.util.Date;

import com.yuxin.messaging.enums.FriendInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendInvitation {
    private int id;
    private int senderUserId;
    private int receiverUserId;
    private String message;
    private Date sendTime;
    private FriendInvitationStatus status;

    public boolean expired() {
        return new Date().getTime() - sendTime.getTime() >= Duration.ofDays(7).toMillis();
    }
}
