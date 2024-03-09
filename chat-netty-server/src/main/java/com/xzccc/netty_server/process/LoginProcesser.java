package com.xzccc.netty_server.process;

import com.xzccc.model.Redis.TokenUser;
import com.xzccc.netty.model.User;
import com.xzccc.netty.model.msg.ProtoMsg;
import com.xzccc.netty_server.session.ServerSession;
import com.xzccc.protoConvertor.LoginResponseConverter;
import com.xzccc.utils.RedisUtils;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

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

    public boolean action(ServerSession session, String token, FullHttpRequest request) {
        TokenUser tokenUser = check(token);
        if (tokenUser == null) {
            // 发送登录失败的报文
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), UNAUTHORIZED);
            session.writeAndFlush(response);
            session.close();
            return false;
        }
        User user = User.fromMsg(tokenUser.getUserId(), token, session.getChannel());
        session.setUser(user);
        session.reverseBind();

        Boolean b = redisUtils.setUserOnline(user.getId());
        if (b == false || b == null) {
            log.error("设置用户：" + user.getId() + " 状态为在线失败");
        }
        return true;
    }

    private TokenUser check(String token) {
        TokenUser tokenUser = redisUtils.getTokenUser(token);
        return tokenUser;
    }
}
