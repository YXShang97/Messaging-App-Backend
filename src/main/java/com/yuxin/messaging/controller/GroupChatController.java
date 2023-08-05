package com.yuxin.messaging.controller;

import com.yuxin.messaging.annotation.NeedAuthentication;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.User;
import com.yuxin.messaging.request.AddGroupChatMemberRequest;
import com.yuxin.messaging.request.CreateGroupChatRequest;
import com.yuxin.messaging.request.UpdateGroupChatRequest;
import com.yuxin.messaging.response.CommonResponse;
import com.yuxin.messaging.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groupChat")
public class GroupChatController {
    @Autowired
    private GroupChatService groupChatService;

    // createGroupChat
    @PostMapping("/create")
    @NeedAuthentication
    public CommonResponse createGroupChat(User user, @RequestBody CreateGroupChatRequest createGroupChatRequest) throws MessagingServiceException {
        this.groupChatService.createGroupChat(user, createGroupChatRequest.getName(), createGroupChatRequest.getDescription());
        return new CommonResponse(Status.OK);
    }

    // addMember
    @PostMapping("/addMember")
    @NeedAuthentication
    public CommonResponse addMember(User user, @RequestBody AddGroupChatMemberRequest addGroupChatMemberRequest) throws MessagingServiceException {
        this.groupChatService.addMember(user, addGroupChatMemberRequest.getGroupChatId(), addGroupChatMemberRequest.getGuestId());
        return new CommonResponse(Status.OK);
    }

    // updateGroupChat <- by creator
    @PostMapping("/update")
    @NeedAuthentication
    public CommonResponse updateGroupChat(User user, @RequestBody UpdateGroupChatRequest updateGroupChatRequest) throws MessagingServiceException {
        this.groupChatService.updateGroupChat(user, updateGroupChatRequest.getId(), updateGroupChatRequest.getName(), updateGroupChatRequest.getDescription());
        return new CommonResponse(Status.OK);
    }
}
