package com.xzccc.netty_server.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

@Service("LoginRequestHandler")
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
}
