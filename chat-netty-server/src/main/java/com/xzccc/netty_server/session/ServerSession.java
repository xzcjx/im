package com.xzccc.netty_server.session;

import com.xzccc.netty.model.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class ServerSession {
    public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");


    private Channel channel;
    private User user;
    private final String sessionId;
    private boolean isLogin = false;

    public ServerSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }

    public Channel getChannel(){
        return channel;
    }
    public static ServerSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(ServerSession.SESSION_KEY).get();
    }

    public static void closeSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        ServerSession session = channel.attr(ServerSession.SESSION_KEY).get();
        if (session != null && session.isValid()) {
            session.close();
            SessionMap.inst().removeSession(session.getSessionId());
        }
    }

    public ServerSession reverseBind() {
        log.info("ServerSession 绑定会话 " + channel.remoteAddress());
        channel.attr(ServerSession.SESSION_KEY).set(this);
        SessionMap.inst().addSession(this);
        isLogin = true;
        return this;
    }

    public ServerSession unbind() {
        isLogin = false;
        SessionMap.inst().removeSession(getSessionId());
        this.close();
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    private boolean isValid() {
        return getUser() != null ? true : false;
    }

    //写Protobuf数据帧
    public synchronized void writeAndFlush(Object pkg) {
        //当系统水位过高时，系统应不继续发送消息，防止发送队列积压
        //写Protobuf数据帧

        if (channel.isWritable()) //低水位
        {
            channel.writeAndFlush(pkg);
        } else {   //高水位时
            log.debug("通道很忙，消息被暂存了");
            //写入消息暂存的分布式存储，如果mongo
            //等channel空闲之后，再写出去
        }
    }

    public synchronized void close() {
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("CHANNEL_CLOSED error ");
                }
            }
        });
    }

    public User getUser() {
        return user;
    }

    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLogin(){
        return isLogin;
    }
}
