package com.xzccc.netty_server.handler;

import com.xzccc.model.Redis.TokenUser;
import com.xzccc.netty_server.model.User;
import com.xzccc.netty_server.session.ServerSession;
import com.xzccc.utils.RedisUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class HttpLoginAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ValueOperations<String, Object> valueOperations;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ChatRedirectHandler chatRedirectHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {

//        String uri = fullHttpRequest.uri();
//        if (!uri.contains("token")) {
//            ctx.writeAndFlush("token无效");
//            ctx.close();
//            return;
//            ctx.fireChannelRead(fullHttpRequest);
//        }
//        fullHttpRequest.retain();
        ctx.pipeline().remove("login");
        ctx.fireChannelRead(fullHttpRequest);
//        String[] split = uri.split("token=");
//        String token = split[split.length-1];
//        TokenUser tokenUser = redisUtils.getTokenUser(token);
//        if (tokenUser==null) {
//            ctx.writeAndFlush("token无效");
//            ctx.close();
//        }
//        long userId = tokenUser.getUserId();
//        User user = new User();
//        user.setId(userId);
//        user.setToken(token);
//        ServerSession session = new ServerSession(ctx.channel(), user);
//        ctx.pipeline().addAfter("encode","chat",chatRedirectHandler);
//        ctx.pipeline().addAfter("login", "heartBeat",new HeartBeatServerHandler());
//        ctx.pipeline().remove("login");
//        log.info("登录成功:" + session.getUser());
//        ctx.writeAndFlush("token无效");
    }
}
