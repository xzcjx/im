package com.xzccc.server.model.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {
    private long uid;
    private String phone;
    private String username;
}
