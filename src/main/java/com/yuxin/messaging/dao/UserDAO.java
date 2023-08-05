package com.yuxin.messaging.dao;

import java.util.Date;
import java.util.List;

import com.yuxin.messaging.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

// data access object
@Mapper
@Repository
public interface UserDAO {
    // user.getUsername();

    @Insert("INSERT INTO user (username, nickname, password, register_time, gender, email, address, is_valid) " +
            "VALUES (#{username}, #{nickname}, #{password}, #{registerTime}, #{gender}, #{email}, #{address}, #{isValid})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM user WHERE email = #{email}")
    List<User> selectByEmail(String email);

    @Select("SELECT * FROM user WHERE username = #{username}")
    List<User> selectByUserName(String username);

    @Update("UPDATE user SET is_valid = 1 WHERE id = #{userId}")
    void updateValid(int userId);

    @Delete("DELETE FROM user")
    void deleteAll();

    // TODO: why @Param is needed?
    @Update("UPDATE user SET login_token = #{loginToken}, last_login_time = #{lastLoginTime, jdbcType=TIMESTAMP} WHERE id = #{userId}")
    void login(@Param("loginToken") String loginToken, @Param("lastLoginTime") Date lastLoginTime, @Param("userId") int userId);

    @Select("SELECT * FROM user WHERE login_token = #{loginToken}")
    User selectUserByLoginToken(String loginToken);

    @Update("UPDATE user SET login_token = #{loginToken}, last_login_time = #{lastLoginTime, jdbcType=TIMESTAMP} WHERE id = #{userId}")
    void logout(@Param("loginToken") String loginToken, @Param("lastLoginTime") Date lastLoginTime, @Param("userId") int userId);

    @Update("UPDATE user SET password = #{password} WHERE id = #{userId}")
    void updatePassword(int userId, String password);

    @Select("SELECT * FROM user WHERE id = #{userId}")
    User selectById(int userId);
}
//
//public class UserDAOImpl implements UserDAO {
//
//    @Override
//    public void insert(User user) {
//        var connector = new Connector();
//        connector.connect();
//        try {
//            connector.executeQuery("INSERT INTO user (username, nickname, password, register_time, gender, email, " +
//                                           "address, is_valid) VALUES (?,?,?,?)", user.getUsername(), user
//                                           .getNickname());\
//        } catch (NetworkException exception) {
//
//        } finally {
//            connector.cleanup();
//        }
//    }
//}
