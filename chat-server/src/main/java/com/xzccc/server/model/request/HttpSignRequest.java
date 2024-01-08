package com.xzccc.server.model.request;

import lombok.Data;

@Data
public class HttpSignRequest {
    private String phone;
    private String username;
    private String password;
}
