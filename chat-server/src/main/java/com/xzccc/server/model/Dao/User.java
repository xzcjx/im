package com.xzccc.server.model.Dao;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private long id;
    private String phone;
    private String username;
    private String password_hash;
    private int role;
    private Date create_at;
}
