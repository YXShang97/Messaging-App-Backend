package com.yuxin.messaging.dao;

import com.yuxin.messaging.model.GroupChatMember;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GroupChatMemberDAO {

    @Insert("INSERT INTO group_chat_member (group_chat_id, member_user_id) " +
            "VALUES (#{groupChatId}, #{userId})")
    void addMember(int groupChatId, int userId);

    @Select("SELECT * FROM group_chat_member WHERE group_chat_id = #{groupChatId} AND member_user_id = #{userId}")
    GroupChatMember selectByGroupChatIdAndUserId(int groupChatId, int userId);

    @Select("SELECT group_chat_id FROM group_chat_member WHERE member_user_id = #{userId}")
    List<Integer> listGroupChatIdsByUserId(int userId);
}
