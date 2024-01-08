package com.xzccc.server.server.impl;

import com.xzccc.server.common.BaseResponse;
import com.xzccc.server.common.ErrorCode;
import com.xzccc.server.exception.BusinessException;
import com.xzccc.server.mapper.UserMapper;
import com.xzccc.server.model.Dao.User;
import com.xzccc.server.model.Vo.HttpLoginResponse;
import com.xzccc.server.model.request.HttpLoginRequest;
import com.xzccc.server.model.request.HttpSignRequest;
import com.xzccc.server.server.HttpLoginUserService;
import com.xzccc.server.utils.HashUtils;
import com.xzccc.server.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;

@Component
public class HttpLoginUserServiceImpl implements HttpLoginUserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    HashUtils hashUtils;

    @Autowired
    JWTUtils jwtUtils;
    @Override
    public BaseResponse login(HttpLoginRequest body) {
        String phone = body.getPhone();
        User user = userMapper.select_by_phone(phone);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_ERROR);
        }
        if (hashUtils.DefaultVerify(body.getPassword(), user.getPassword_hash()) == true) {
            return new BaseResponse(200, new HttpLoginResponse(user.getId(), jwtUtils.getToken(new HashMap<String, Long>() {
                {
                    put("sup", user.getId());
                }
            })));
        }
        throw new BusinessException(ErrorCode.PASSWORD_ERROR);
    }

    @Override
    public BaseResponse sign(HttpSignRequest body) {
        String username = body.getUsername();
        String phone = body.getPhone();
        User user = userMapper.select_by_phone(phone);
        if (user != null) {
            throw new BusinessException(ErrorCode.USERNAME_ERROR);
        }
        String password = body.getPassword();
        String password_hash = hashUtils.DefaultHash(password);
        try {
            userMapper.insert(phone, username, password_hash);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return new BaseResponse(200, true);
    }
}
