package com.yuxin.messaging.service;

import com.google.common.cache.LoadingCache;
import com.yuxin.messaging.dao.GroupChatDAO;
import com.yuxin.messaging.dao.GroupChatMemberDAO;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.GroupChat;
import com.yuxin.messaging.model.GroupChatMember;
import com.yuxin.messaging.model.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GroupChatService {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private GroupChatDAO groupChatDAO;
    @Autowired
    private GroupChatMemberDAO groupChatMemberDAO;

    @Autowired
    @Qualifier("userGroupChatCache")
    private LoadingCache<Integer, List<Integer>> userGroupChatCache;

    public void createGroupChat(User user, String name, String description) {
        GroupChat groupChat = GroupChat.builder()
                                        .name(name)
                                        .description(description)
                                        .creatorUserId(user.getId())
                                        .createTime(new Date())
                                        .build();
        this.groupChatDAO.createGroupChat(groupChat);
        this.groupChatMemberDAO.addMember(groupChat.getId(), user.getId());
    }

    public void addMember(User user, int groupChatId, int guestId) throws MessagingServiceException {
        // check whether group chat exists
        GroupChat groupChat = this.groupChatDAO.selectGroupChatById(groupChatId);
        if (groupChat == null) {
            throw new MessagingServiceException(Status.GROUP_CHAT_NOT_EXISTS);
        }
        // check whether user in group chat
        GroupChatMember groupChatMember = this.groupChatMemberDAO.selectByGroupChatIdAndUserId(groupChatId, user.getId());
        if (groupChatMember == null) {
            throw new MessagingServiceException(Status.USER_NOT_IN_GROUP_CHAT);
        }
        // check whether user and guest are friends
        boolean verifiedFriends = this.friendService.verifyFriends(user.getId(), guestId);
        if (!verifiedFriends) {
            throw new MessagingServiceException(Status.GUEST_NOT_YOUR_FRIEND);
        }
        // check whether guest already in group chat
        User guest = this.userService.selectById(guestId);
        GroupChatMember groupChatMember1 = this.groupChatMemberDAO.selectByGroupChatIdAndUserId(groupChatId, guestId);
        if (groupChatMember1 != null) {
            throw new MessagingServiceException(Status.GUEST_ALREADY_IN_GROUP_CHAT);
        }
        this.groupChatMemberDAO.addMember(groupChatId, guestId);
    }


    public void updateGroupChat(User user, int id, String name, String description) throws MessagingServiceException {
        // check whether group chat exists
        GroupChat groupChat = this.groupChatDAO.selectGroupChatById(id);
        if (groupChat == null) {
            throw new MessagingServiceException(Status.GROUP_CHAT_NOT_EXISTS);
        }
        // check whether user is creator
        if (groupChat.getCreatorUserId() != user.getId()) {
            throw new MessagingServiceException(Status.USER_NOT_CREATOR);
        }
        // update group chat
        groupChat.setName(name);
        groupChat.setDescription(description);
        // update group chat in database
        this.groupChatDAO.updateGroupChat(groupChat);
    }

    public List<Integer> listGroupChatIds(int userId) throws Exception {
        return this.userGroupChatCache.get(userId);
    }
}
