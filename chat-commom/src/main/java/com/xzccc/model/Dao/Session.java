package com.xzccc.model.Dao;

import lombok.Data;

@Data
public class Session {
    private Long id;
    private String sessionId;
    private Long userId;
    private Long friendId;
    private Short status;
}
