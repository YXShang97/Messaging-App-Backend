package com.yuxin.messaging.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.common.cache.LoadingCache;
import com.yuxin.messaging.dao.MessageDAO;
import com.yuxin.messaging.enums.MessageType;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.Message;
import com.yuxin.messaging.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    @Qualifier("friendCache")
    private LoadingCache<Integer, List<Integer>> friendCache;

    @Autowired
    @Qualifier("userGroupChatCache")
    private LoadingCache<Integer, List<Integer>> userGroupChatCache;

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private AmazonS3 amazonS3;

    public void sendMessage(User sender,
                            Integer receiverUserId,
                            Integer groupChatId,
                            MessageType messageType,
                            String content,
                            InputStream fileInputStream) throws Exception {
        this.validate(sender, receiverUserId, groupChatId);
        Message message = Message.builder()
                .senderUserId(sender.getId())
                .receiverUserId(receiverUserId)
                .groupChatId(groupChatId)
                .messageType(messageType)
                .sendTime(new Date())
                .content(content)
                .build();
        this.messageDAO.insert(message);
        int messageId = message.getId();
        boolean valid = false;
        try {
            if (messageType != MessageType.TEXT) {
                this.amazonS3.putObject("yuxin-messaging-user-file",
                                        String.valueOf(messageId),
                                        fileInputStream,
                                        new ObjectMetadata());
            }
            valid = true;
        } finally {
            this.messageDAO.updateValid(messageId, valid);
        }
    }

    private void validate(User sender, Integer receiverUserId, Integer groupChatId) throws Exception {
        if (receiverUserId == null && groupChatId == null) {
            throw new MessagingServiceException(Status.RECEIVER_NOT_FOUND);
        }

        // check sender is in the group chat
        List<Integer> groupChatIds = this.userGroupChatCache.get(sender.getId());
        if (groupChatId != null && !groupChatIds.contains(groupChatId)) {
            throw new MessagingServiceException(Status.SENDER_NOT_IN_GROUP_CHAT);
        }

        // check sender and receiver are friends
        List<Integer> friendIds = this.friendCache.get(sender.getId());
        if (receiverUserId != null && !friendIds.contains(receiverUserId)) {
            throw new MessagingServiceException(Status.SENDER_AND_RECEIVER_NOT_FRIEND);
        }
    }

    public List<Message> listMessages(User user, Integer receiverId, Integer groupChatId, Integer page) throws Exception {
        this.validate(user, receiverId, groupChatId);
        if (receiverId != null) {
            return this.messageDAO.listMessagesByReceiverId(user.getId(), receiverId, page);
        } else {
            return this.messageDAO.listMessagesByGroupChatId(user.getId(), groupChatId, page);
        }
    }

    public MessageType getMessageType(Integer messageId) throws MessagingServiceException {
        Message message = this.messageDAO.getMessage(messageId);
        // check if message exists
        if (message == null) {
            throw new MessagingServiceException(Status.MESSAGE_NOT_FOUND);
        }
        return message.getMessageType();
    }

    public byte[] getMessageFile(User user, Integer messageId) throws MessagingServiceException {
        try {
            return this.amazonS3.getObject("yuxin-messaging-user-file", String.valueOf(messageId))
                    .getObjectContent()
                    .readAllBytes();
        } catch (Exception e) {
            throw new MessagingServiceException(Status.UNKNOWN_EXCEPTION);
        }
    }
}
