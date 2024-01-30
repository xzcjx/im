package com.xzccc.netty_server.process;

import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty_server.session.ServerSession;

public interface ServerProcesser {

    ProtoMsg.HeadType type();

    boolean action(ServerSession ch, ProtoMsg.Message proto);

}