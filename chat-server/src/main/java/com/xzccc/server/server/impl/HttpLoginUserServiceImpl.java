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
import com.xzccc.server.utils.RedisUtils;
import com.xzccc.server.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;

@Component
public class HttpLoginUserServiceImpl implements HttpLoginUserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    HashUtils hashUtils;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    RedisUtils redisUtils;



    @Override
    public BaseResponse login(HttpLoginRequest body) {
        String phone = body.getPhone();
        User user = userMapper.select_by_phone(phone);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_ERROR);
        }
        if (hashUtils.DefaultVerify(body.getPassword(), user.getPassword_hash()) == true) {

            String uuid32 = tokenUtils.getUUID32();
            redisUtils.setUserToken(user.getId()+"",uuid32);
            redisUtils.setTokenUser(user.getId()+"",uuid32);
            return new BaseResponse(200, new HttpLoginResponse(user.getId(), uuid32));
        }
        throw new BusinessException(ErrorCode.PASSWORD_ERROR);
    }

    @Transactional
    @Override
    public BaseResponse sign(HttpSignRequest body) {
        String username = body.getUsername();
        String phone = body.getPhone();
        String password = body.getPassword();

        if(StringUtils.isBlank(username)|| StringUtils.isBlank(phone)|| StringUtils.isBlank(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userMapper.select_by_phone(phone);
        if (user != null) {
            throw new BusinessException(ErrorCode.USERNAME_ERROR);
        }
        user = new User();

        user.setPassword_hash(hashUtils.DefaultHash(password));
        user.setPhone(phone);
        user.setUsername(username);
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return new BaseResponse(200, true);
    }
}
