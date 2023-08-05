package com.yuxin.messaging.service;

import com.google.common.cache.LoadingCache;
import com.yuxin.messaging.dao.FriendInvitationDAO;
import com.yuxin.messaging.enums.FriendInvitationStatus;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.FriendInvitation;
import com.yuxin.messaging.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FriendService {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendInvitationDAO friendInvitationDAO;

    @Autowired
    @Qualifier("friendCache")
    private LoadingCache<Integer, List<Integer>> friendCache;

    public void addFriendInvitation(User sender, int receiverUserId, String message) throws MessagingServiceException {
        // 1. verify they are not friends
        if (this.verifyFriends(sender.getId(), receiverUserId)) {
            throw new MessagingServiceException(Status.ALREADY_FRIENDS);
        };
        // 2. receiver exists already
        User receiver = this.userService.selectById(receiverUserId);
        if (receiver == null) {
            throw new MessagingServiceException(Status.RECEIVER_NOT_EXISTS);
        }
        // 3. sender has not sent an invitation within 7 days to the receiver
        // 4. there are no pending invitations between them
        List<FriendInvitation> invitations = this.friendInvitationDAO.selectInvitationsBySenderAndReceiver(sender.getId(), receiverUserId);
        for (FriendInvitation invitation : invitations) {
            if (invitation.getStatus() == FriendInvitationStatus.PENDING) {
                throw new MessagingServiceException(Status.INVITATION_IS_PENDING);
            }
            if (!invitation.expired()) {
                throw new MessagingServiceException(Status.INVITATION_TOO_FREQUENT);
            }
        }

        // 5. create a new invitation
        FriendInvitation newInvitation = FriendInvitation.builder()
                                                        .senderUserId(sender.getId())
                                                        .receiverUserId(receiverUserId)
                                                        .message(message)
                                                        .status(FriendInvitationStatus.PENDING)
                                                        .sendTime(new Date())
                                                        .build();
        this.friendInvitationDAO.insert(newInvitation);
    }

    public boolean verifyFriends(int senderId, int receiverId) {
        List<FriendInvitation> acceptedInvitations = this.friendInvitationDAO.selectAcceptedInvitationBySenderAndReceiver(senderId, receiverId);
        if (acceptedInvitations != null && !acceptedInvitations.isEmpty()) {
            return true;
        } else {
            acceptedInvitations = this.friendInvitationDAO.selectAcceptedInvitationBySenderAndReceiver(receiverId, senderId);
            if (acceptedInvitations != null && !acceptedInvitations.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<FriendInvitation> listFriendInvitations(User user, int page) {
        List<FriendInvitation> invitations = this.friendInvitationDAO.selectInvitationByReceiver(user.getId(), page);
        return invitations;
    }

    public void acceptFriendInvitation(User user, int friendInvitationId) throws MessagingServiceException {
        FriendInvitation invitation = selectInvitationByReceiverAndId(user, friendInvitationId);
        this.friendInvitationDAO.updateStatusToAccepted(invitation);
    }

    public void rejectFriendInvitation(User user, int friendInvitationId) throws MessagingServiceException {
        FriendInvitation invitation = selectInvitationByReceiverAndId(user, friendInvitationId);
        this.friendInvitationDAO.updateStatusToRejected(invitation);
    }

    private FriendInvitation selectInvitationByReceiverAndId(User user, int friendInvitationId) throws MessagingServiceException {
        FriendInvitation invitation = this.friendInvitationDAO.selectInvitationById(friendInvitationId);
        if (invitation == null) {
            throw new MessagingServiceException(Status.INVITATION_NOT_EXISTS);
        }
        if (invitation.getReceiverUserId() != user.getId()) {
            throw new MessagingServiceException(Status.INVITATION_NOT_FOR_YOU);
        }
        if (invitation.getStatus() != FriendInvitationStatus.PENDING) {
            throw new MessagingServiceException(Status.INVITATION_NOT_PENDING);
        }
        return invitation;
    }

    public List<User> listFriends(User user) {
        List<User> friends = this.friendInvitationDAO.selectFriendsByUserId(user.getId());
        return friends;
    }

    public List<Integer> listFriendIds(int userId) throws Exception {
        return this.friendCache.get(userId);
    }
}
