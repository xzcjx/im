package com.xzccc.netty_server.model;

import com.xzccc.netty.model.msg.ProtoMsg;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String token;
    private String ip;
    private String port;

    public static User fromMsg(ProtoMsg.LoginRequest msg, Channel ctx){
        long userId = msg.getUserId();
        String token = msg.getToken();
        User user = new User();
        user.setId(userId);
        user.setToken(token);
        user.setIp(ctx.remoteAddress().toString());
        return user;
    }
}
