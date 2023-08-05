package com.yuxin.messaging.dao;

import com.yuxin.messaging.model.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDAO {
    @Insert("INSERT INTO messages (sender_user_id, receiver_user_id, group_chat_id, content, message_type, send_time, valid) values (#{senderUserId}, #{receiverUserId}, #{groupChatId}, #{content}, #{type}, #{sendTime}, 0)")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insert(Message message);

    @Update("UPDATE messages SET valid = #{valid} WHERE id = #{messageId}")
    void updateValid(int messageId, boolean valid);

    @Select("SELECT * FROM messages WHERE id = #{messageId}")
    Message getMessage(Integer messageId);

    @Select("SELECT * FROM messages WHERE sender_user_id = #{senderId} AND receiver_user_id = #{receiverId} AND valid = 1 ORDER BY send_time DESC LIMIT ${(page - 1) * 10}, 10")
    List<Message> listMessagesByReceiverId(int senderId, Integer receiverId, Integer page);

    @Select("SELECT * FROM messages WHERE sender_user_id = #{senderId} AND group_chat_id = #{groupChatId} AND valid = 1 ORDER BY send_time DESC LIMIT ${(page - 1) * 10}, 10")
    List<Message> listMessagesByGroupChatId(int senderId, Integer groupChatId, Integer page);
}
