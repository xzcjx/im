package com.xzccc.server.model.Dao;

import lombok.Data;

import java.util.Date;

@Data
public class FriendShip {
    private Long id;
    private Long user_id;
    private Long friend_id;
    private Short status;
    private String ps;
    private String note;
    private Date created_at;
    private Date deleted_at;
}
