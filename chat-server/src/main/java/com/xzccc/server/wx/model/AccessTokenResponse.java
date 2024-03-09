package com.xzccc.server.wx.model;

import lombok.Data;

@Data
public class AccessTokenResponse {
    private String access_token;
    private long expires_in;
}
