package com.xzccc.server.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    JWTUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        DecodedJWT decodedJWT;
        try {
            authorization = authorization.replaceFirst("Bearer ", "");
            decodedJWT = jwtUtils.decodeToken(authorization);
            Date current_date = new Date();
            if (current_date.after(decodedJWT.getExpiresAt())) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            Claim sup = decodedJWT.getClaim("sup");
            Long uid = sup.asLong();
            request.getSession().setAttribute("uid",uid);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return true;
    }
}
