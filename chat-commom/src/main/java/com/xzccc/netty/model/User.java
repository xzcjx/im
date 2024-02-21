package com.xzccc.netty.model;

import com.xzccc.model.Redis.TokenUser;
import com.xzccc.netty.model.msg.ProtoMsg;
import io.netty.channel.Channel;
import lombok.Data;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Data
public class User {
    private Long id;
    private String token;
    private String ip;
    private int port;

    public static User fromMsg(long userId,String token, Channel ctx){
        User user = new User();
        user.setId(userId);
        user.setToken(token);
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.remoteAddress();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        user.setIp(hostAddress);
        user.setPort(port);
        return user;
    }
}
