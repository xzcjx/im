package com.xzccc.server.model.Dao;

import lombok.Data;

import java.util.Date;

@Data
public class FriendShip {
    private long id;
    private long uid;
    private long friend_id;
    private String note;
    private int delete;
    private Date create_at;
}
