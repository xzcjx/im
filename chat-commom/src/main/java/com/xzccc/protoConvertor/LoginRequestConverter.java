package com.xzccc.protoConvertor;

import com.xzccc.netty.constant.ProtoInstant;
import com.xzccc.netty.model.User;
import com.xzccc.netty.model.msg.ProtoMsg;

import java.util.Random;

public class LoginRequestConverter{
    public ProtoMsg.Message build(User user){
        int seqId= 1;
        ProtoMsg.Message.Builder builder = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.LOGIN_RESPONSE)
                .setSequence(seqId)
                .setSessionId("aaa");
        ProtoMsg.LoginRequest.Builder b = ProtoMsg.LoginRequest.newBuilder()
                .setUserId(user.getId())
                .setToken(user.getToken());
        builder.setLoginRequest(b);
        return builder.build();
    }
}
