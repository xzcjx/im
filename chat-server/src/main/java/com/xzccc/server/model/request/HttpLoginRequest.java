package com.xzccc.server.model.request;

import lombok.Data;

@Data
public class HttpLoginRequest {
    private String phone;
    private String password;
}
