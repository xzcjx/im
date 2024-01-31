package com.xzccc.netty_server.protoConvertor;

import com.xzccc.netty_server.constant.ProtoInstant;
import com.xzccc.netty_server.model.msg.ProtoMsg;
import org.springframework.stereotype.Service;

@Service
public class LoginResponseConverter {
    public ProtoMsg.Message build(ProtoInstant.ResultCodeEnum en,long seqId,String sessionId){
        ProtoMsg.Message.Builder builder = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.LOGIN_RESPONSE)
                .setSequence(seqId)
                .setSessionId(sessionId);
        ProtoMsg.LoginResponse.Builder b = ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);
        builder.setLoginResponse(b);
        return builder.build();
    }
}
