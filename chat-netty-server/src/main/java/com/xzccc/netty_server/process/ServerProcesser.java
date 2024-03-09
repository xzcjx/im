package com.xzccc.netty_server.process;

import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty_server.session.ServerSession;
import io.netty.handler.codec.http.FullHttpRequest;

public interface ServerProcesser {

    ProtoMsg.HeadType type();

    default boolean action(ServerSession ch, String token, FullHttpRequest request) {
        return true;
    }

    ;

    default boolean action(ServerSession session, ProtoMsg.Message proto) {
        return true;
    }

    ;
}
