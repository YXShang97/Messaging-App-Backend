package com.yuxin.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupChat {
    private int id;
    private String name;
    private String description;
    private int creatorUserId;
    private Date createTime;
}
