package com.xzccc.server.model.Vo;

import lombok.Data;

@Data
public class HttpLoginResponse {
    private long user_id;
    private String token;
    public HttpLoginResponse(long user_id,String token){
        this.user_id=user_id;
        this.token=token;
    }

}
