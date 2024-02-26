package com.xzccc.server.utils;

import lombok.Data;

import java.util.Date;

@Data
public class TextMessage {
    private String toUserName;
    private String fromUserName;
    private long createTime;
    private String msgType;
    private String content;
}
