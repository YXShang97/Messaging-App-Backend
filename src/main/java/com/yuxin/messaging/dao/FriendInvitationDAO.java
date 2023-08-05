package com.yuxin.messaging.dao;

import com.yuxin.messaging.model.FriendInvitation;
import com.yuxin.messaging.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FriendInvitationDAO {
    @Select("SELECT * FROM friend_invitation WHERE sender_user_id = #{id} AND receiver_user_id = #{receiverUserId} AND status = 'ACCEPTED'")
    List<FriendInvitation> selectAcceptedInvitationBySenderAndReceiver(int id, int receiverUserId);

    @Select("SELECT * FROM friend_invitation WHERE sender_user_id = #{senderId} AND receiver_user_id = #{receiverId}")
    List<FriendInvitation> selectInvitationsBySenderAndReceiver(int senderId, int receiverId);

    @Insert("INSERT INTO friend_invitation (sender_user_id, receiver_user_id, message, send_time, status) " +
            "VALUES (#{senderUserId}, #{receiverUserId}, #{message}, #{sendTime}, #{status})")
    void insert(FriendInvitation newInvitation);

    @Select("SELECT * FROM friend_invitation WHERE receiver_user_id = #{id} AND status = 'PENDING' ORDER BY send_time DESC LIMIT ${(page - 1) * 10}, 10")
    List<FriendInvitation> selectInvitationByReceiver(int id, int page);

    @Select("SELECT * FROM friend_invitation WHERE id = #{friendInvitationId}")
    FriendInvitation selectInvitationById(int friendInvitationId);

    @Update("UPDATE friend_invitation SET status = 'ACCEPTED' WHERE id = #{id}")
    void updateStatusToAccepted(FriendInvitation invitation);

    @Update("UPDATE friend_invitation SET status = 'REJECTED' WHERE id = #{id}")
    void updateStatusToRejected(FriendInvitation invitation);

    @Select("SELECT * FROM user WHERE id IN (SELECT sender_user_id FROM friend_invitation WHERE receiver_user_id = #{id} AND status = 'ACCEPTED') OR id IN (SELECT receiver_user_id FROM friend_invitation WHERE sender_user_id = #{id} AND status = 'ACCEPTED')")
    List<User> selectFriendsByUserId(int id);

    @Select("SELECT sender_user_id FROM friend_invitation WHERE receiver_user_id = #{id} AND status = 'ACCEPTED' UNION SELECT receiver_user_id FROM friend_invitation WHERE sender_user_id = #{id} AND status = 'ACCEPTED'")
    List<Integer> listFriendIdsByUserId(int id);
}
