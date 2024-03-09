package com.xzccc.netty_server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
        }

        if (frame instanceof PingWebSocketFrame) {
        }

        if (frame instanceof TextWebSocketFrame) {
            log.info(String.valueOf(frame));
            ctx.channel().writeAndFlush(frame);
            ctx.close();
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            sendErrorMessage(ctx, "仅支持文本(Text)格式，不支持二进制消息");
        }
    }

    private void sendErrorMessage(ChannelHandlerContext ctx, String s) {
        String responseJson = "不支持二进制消息";
        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseJson));
    }
}
