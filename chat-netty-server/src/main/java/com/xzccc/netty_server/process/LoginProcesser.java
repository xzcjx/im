package com.xzccc.netty_server.process;

import com.xzccc.model.Redis.TokenUser;
import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty.constant.ProtoInstant;
import com.xzccc.netty.model.User;
import com.xzccc.protoConvertor.LoginResponseConverter;
import com.xzccc.netty_server.session.ServerSession;
import com.xzccc.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginProcesser implements ServerProcesser {
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    LoginResponseConverter loginResponseConverter;

    @Override
    public ProtoMsg.HeadType type() {
        return ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public boolean action(ServerSession session, ProtoMsg.Message proto) {
        ProtoMsg.LoginRequest loginRequest = proto.getLoginRequest();
        Boolean r = check(loginRequest);
        if (r == false) {
            ProtoInstant.ResultCodeEnum resultcode =
                    ProtoInstant.ResultCodeEnum.NO_TOKEN;
            //构造登录失败的报文
            ProtoMsg.Message response =
                    loginResponseConverter.build(resultcode, "-1");
            //发送登录失败的报文
            session.writeAndFlush(response);
            return false;
        }
        User user = User.fromMsg(loginRequest, session.getChannel());
        session.setUser(user);
        session.reverseBind();

        Boolean b = redisUtils.setUserOnline(user.getId());
        if (b==false||b==null) {
            log.error("设置用户："+user.getId()+" 状态为在线失败");
        }

        ProtoInstant.ResultCodeEnum resultcode = ProtoInstant.ResultCodeEnum.SUCCESS;
        ProtoMsg.Message response = loginResponseConverter.build(resultcode, session.getSessionId());
        session.writeAndFlush(response);
        return true;
    }

    private Boolean check(ProtoMsg.LoginRequest loginRequest) {
        long userId = loginRequest.getUserId();
        String token = loginRequest.getToken();
        TokenUser tokenUser = redisUtils.getTokenUser(token);
        if (tokenUser == null) {
            return false;
        }
        return tokenUser.getUserId() == userId ? true : false;
    }
}
