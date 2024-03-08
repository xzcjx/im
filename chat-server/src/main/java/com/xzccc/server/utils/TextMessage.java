package com.xzccc.server.utils;

import lombok.Data;

@Data
public class TextMessage {
    private String toUserName;
    private String fromUserName;
    private long createTime;
    private String msgType;
    private String content;
}
