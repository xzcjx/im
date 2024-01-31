package com.xzccc.model.Dao;

import lombok.Data;

@Data
public class Session {
    private Long id;
    private String session_id;
    private Long user_id;
    private Long friend_id;
    private Short status;
}
