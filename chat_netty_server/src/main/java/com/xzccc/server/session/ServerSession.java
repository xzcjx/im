package com.xzccc.server.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;


public class ServerSession {
    public static final AttributeKey<String> KEY_USER_ID =      AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<ServerSession> SESSION_KEY =    AttributeKey.valueOf("SESSION_KEY");


    private Channel channel;
    private User user;
    private final String sessionId;
    private boolean isLogin=false;

    public ServerSession(Channel channel){
        this.channel=channel;
        this.sessionId=buildNewSessionId();
    }
    public ServerSession(String sessionId){
        this.sessionId=sessionId;
    }

    public static ServerSession getSession(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        return channel.attr(ServerSession.SESSION_KEY).get();
    }

    public static void closeSession(ChannelHandlerContext ctx){
        Channel channel = ctx.channel();
        ServerSession session = ctx.attr(ServerSession.SESSION_KEY).get();
        if(session!=null&& session.isValid()){
//            session.close();
            // redis移除
        }

    }

    private boolean isValid() {
        return getUser() != null ? true : false;
    }

    private User getUser() {
        return user;
    }

    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void setUser(User user) {
        this.user = user;
        user.setSessionId(sessionId);
    }
}
