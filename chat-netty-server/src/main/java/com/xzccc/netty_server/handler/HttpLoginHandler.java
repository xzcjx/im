package com.xzccc.netty_server.handler;

import com.xzccc.concurrent.CallbackTask;
import com.xzccc.concurrent.CallbackTaskScheduler;
import com.xzccc.netty_server.process.LoginProcesser;
import com.xzccc.netty_server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

@Service
@Slf4j
@ChannelHandler.Sharable
public class HttpLoginHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    CallbackTaskScheduler callbackTaskScheduler;

    @Autowired
    LoginProcesser loginProcesser;
    @Autowired
    ChatRedirectHandler chatRedirectHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            String[] split = uri.split("token=");
            if (split.length >= 2) {
                String token = split[1];
                ServerSession session = new ServerSession(ctx.channel());

                callbackTaskScheduler.add(
                        new CallbackTask<Boolean>() {
                            @Override
                            public Boolean execute() throws Exception {
                                boolean action = loginProcesser.action(session, token, request);
                                return action;
                            }

                            @Override
                            public void onBack(Boolean r) {
                                if (r) {

                                    ctx.pipeline()
                                            .addBefore(
                                                    "serverExceptionHandler", "heartBeat", new HeartBeatServerHandler());
                                    ctx.pipeline().addBefore("serverExceptionHandler", "chat", chatRedirectHandler);

                                    ctx.pipeline().remove("login");
                                    log.info("登录成功:" + session.getUser());
                                } else {
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
                super.channelRead(ctx, msg);
            }
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), UNAUTHORIZED);
            ctx.writeAndFlush(response);
            return;

        } else {
            super.channelRead(ctx, msg);
        }
    }
}
