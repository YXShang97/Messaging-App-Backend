package com.yuxin.messaging.model;

import java.util.Date;

import com.yuxin.messaging.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// model / data transfer object / DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Map.of("username", "11111", "nickname", "11111", "register_time", "xxxxxxxx")
public class User {
    private int id;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String address;
    private Gender gender;
    private Date registerTime;
    private Boolean isValid;
    private String loginToken;
    private Date lastLoginTime;
}
