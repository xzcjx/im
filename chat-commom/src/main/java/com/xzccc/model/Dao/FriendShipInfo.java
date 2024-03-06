package com.xzccc.model.Dao;

import lombok.Data;

import java.util.Date;

@Data
public class FriendShipInfo {
    private Long user_id;
    private Long friend_id;
    private Short read;
    private Short status;
    private Short ps;
    private Date created_at;
}
