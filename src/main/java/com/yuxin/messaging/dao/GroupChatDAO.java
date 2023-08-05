package com.yuxin.messaging.dao;

import com.yuxin.messaging.model.GroupChat;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface GroupChatDAO {

    @Insert("INSERT INTO group_chat (name, description, creator_user_id, create_time) " +
            "VALUES (#{name}, #{description}, #{creatorUserId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void createGroupChat(GroupChat groupChat);

    @Select("SELECT * FROM group_chat WHERE id = #{groupChatId}")
    GroupChat selectGroupChatById(int groupChatId);

    @Update("UPDATE group_chat SET name = #{name}, description = #{description} WHERE id = #{id}")
    void updateGroupChat(GroupChat groupChat);
}
