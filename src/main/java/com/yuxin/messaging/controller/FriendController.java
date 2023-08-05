package com.yuxin.messaging.controller;


import com.yuxin.messaging.annotation.NeedAuthentication;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.FriendInvitation;
import com.yuxin.messaging.model.User;
import com.yuxin.messaging.request.AddFriendRequest;
import com.yuxin.messaging.response.CommonResponse;
import com.yuxin.messaging.response.ListFriendResponse;
import com.yuxin.messaging.response.ListInvitationsResponse;
import com.yuxin.messaging.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {
    @Autowired
    FriendService friendService;

    @PostMapping("/add")
    @NeedAuthentication
    public CommonResponse addFriend(User user, @RequestBody AddFriendRequest addFriendRequest) throws MessagingServiceException {
        this.friendService.addFriendInvitation(user, addFriendRequest.getReceiverUserId(), addFriendRequest.getMessage());
        return new CommonResponse(Status.OK);
    }

    @GetMapping("/listInvitations")
    @NeedAuthentication
    public ListInvitationsResponse listInvitations(User user, @RequestParam int page) {
        List<FriendInvitation> invitations = this.friendService.listFriendInvitations(user, page);
        return ListInvitationsResponse.builder()
                                      .friendInvitations(invitations)
                                      .build();
    }

    @PostMapping("/accept")
    @NeedAuthentication
    public CommonResponse acceptInvitation(User user, @RequestParam int friendInvitationId) throws MessagingServiceException {
        this.friendService.acceptFriendInvitation(user, friendInvitationId);
        return new CommonResponse(Status.OK);
    }

    @PostMapping("/reject")
    @NeedAuthentication
    public CommonResponse rejectInvitation(User user, @RequestParam int friendInvitationId) throws MessagingServiceException {
        this.friendService.rejectFriendInvitation(user, friendInvitationId);
        return new CommonResponse(Status.OK);
    }

    @GetMapping("/listFriends")
    @NeedAuthentication
    public ListFriendResponse listFriends(User user) {
        List<User> friends = this.friendService.listFriends(user);
        return ListFriendResponse.builder()
                .friends(friends)
                .build();
    }

}
