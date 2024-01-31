package com.xzccc.netty_server.handler;

import com.xzccc.concurrent.CallbackTask;
import com.xzccc.concurrent.CallbackTaskScheduler;
import com.xzccc.netty_server.model.msg.ProtoMsg;
import com.xzccc.netty_server.process.LoginProcesser;
import com.xzccc.netty_server.session.ServerSession;
import com.xzccc.utils.RedisUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ValueOperations<String, Object> valueOperations;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ChatRedirectHandler chatRedirectHandler;

    @Autowired
    LoginProcesser loginProcesser;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg==null||!(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx,msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();

        if(headType!=loginProcesser.type()){
            super.channelRead(ctx,msg);
            return;
        }

        ServerSession session = new ServerSession(ctx.channel());

        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                boolean action = loginProcesser.action(session, pkg);
                return action;
            }

            @Override
            public void onBack(Boolean r) {
                if(r){
                    ctx.pipeline().addAfter("login", "chat",   chatRedirectHandler);
                    ctx.pipeline().addAfter("login", "heartBeat",new HeartBeatServerHandler());

                    ctx.pipeline().remove("login");
                    log.info("登录成功:" + session.getUser());
                }else {
                    ServerSession.closeSession(ctx);
                    log.info("登录失败:" + session.getUser());
                }
            }

            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(ctx);
                log.info("登录失败:" + session.getUser());
            }
        });


    }
}
