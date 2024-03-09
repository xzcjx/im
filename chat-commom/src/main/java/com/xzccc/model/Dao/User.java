package com.xzccc.model.Dao;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private long id;
    private String email;
    private String account;
    private String username;
    private String passwordHash;
    private int status = 1;
    private Date create_at;
    private String avatar;
}
