package com.xzccc.protoConvertor;

import com.xzccc.netty.model.User;
import com.xzccc.netty.model.msg.ProtoMsg;

public class LoginRequestConverter {
  public ProtoMsg.Message build(User user) {
    ProtoMsg.Message.Builder builder =
        ProtoMsg.Message.newBuilder().setType(ProtoMsg.HeadType.LOGIN_REQUEST).setSessionId("aaa");
    ProtoMsg.LoginRequest.Builder b =
        ProtoMsg.LoginRequest.newBuilder().setUserId(user.getId()).setToken(user.getToken());
    builder.setLoginRequest(b);
    return builder.build();
  }
}
