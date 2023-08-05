package com.yuxin.messaging.dao;

import com.yuxin.messaging.model.UserValidationCode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserValidationCodeDAO {
    @Insert("INSERT INTO user_validation_code (user_id, validation_code)" +
            "VALUES (#{userId}, #{validationCode})")
    void insert(UserValidationCode userValidationCode);

    @Select("SELECT validation_code FROM user_validation_code WHERE user_id = #{userId}")
    String selectByUserId(int userId);

    @Delete("DELETE FROM user_validation_code WHERE user_id = #{userId}")
    void deleteByUserId(int userId);

    @Delete("DELETE FROM user_validation_code")
    void deleteAll();
}
