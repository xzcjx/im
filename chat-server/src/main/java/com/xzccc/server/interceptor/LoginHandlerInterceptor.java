package com.xzccc.server.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.model.Redis.TokenUser;
import com.xzccc.server.utils.RedisUtils;
import com.xzccc.server.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ThreadLocalUtils threadLocalUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        authorization = authorization.replaceFirst("Bearer ", "");
        TokenUser tokenUser = redisUtils.getTokenUser(authorization);
        if (tokenUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        long userId = tokenUser.getUser_id();
        Long exp = tokenUser.getExp();
        long time = new Date().getTime();
        if (time >= exp) {
            throw new BusinessException(ErrorCode.EXP_AUTH);
        }

        threadLocalUtils.set(userId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        threadLocalUtils.remove();
    }
}
