package com.xzccc.protoConvertor;

import com.xzccc.netty.constant.ProtoInstant;
import com.xzccc.netty.model.msg.ProtoMsg;
import org.springframework.stereotype.Service;

@Service
public class LoginResponseConverter {
    public ProtoMsg.Message build(ProtoInstant.ResultCodeEnum en, String sessionId) {
        ProtoMsg.Message.Builder builder =
                ProtoMsg.Message.newBuilder().setType(ProtoMsg.HeadType.LOGIN_RESPONSE);
        ProtoMsg.LoginResponse.Builder b =
                ProtoMsg.LoginResponse.newBuilder()
                        .setCode(en.getCode())
                        .setInfo(en.getDesc())
                        .setExpose(1);
        builder.setLoginResponse(b);
        return builder.build();
    }
}
