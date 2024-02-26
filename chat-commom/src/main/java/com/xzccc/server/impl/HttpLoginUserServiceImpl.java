package com.xzccc.server.impl;


import com.xzccc.common.BaseResponse;
import com.xzccc.common.ErrorCode;
import com.xzccc.exception.BusinessException;
import com.xzccc.mapper.UserMapper;
import com.xzccc.model.Dao.User;
import com.xzccc.model.Redis.TokenUser;
import com.xzccc.model.Redis.UserToken;
import com.xzccc.model.Vo.HttpLoginResponse;
import com.xzccc.model.request.HttpLoginRequest;
import com.xzccc.model.request.HttpSignRequest;
import com.xzccc.server.HttpLoginUserService;

import com.xzccc.utils.HashUtils;
import com.xzccc.utils.RedisUtils;
import com.xzccc.utils.RedissonUtils;
import com.xzccc.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;

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

    @Autowired
    RedissonUtils redissonutils;

    @Value("${token.exp}")
    long token_exp;

    @Override
    public BaseResponse login(HttpLoginRequest body) {
        String account = body.getAccount();
        String type = body.getType();
        if("phone".equals(type)){
            // 手机号登录模式
        } else if ("account".equals(type)) {
            // 账号登录模式
        }
//        User user = userMapper.select_by_phone(phone);
//        if (user == null) {
//            throw new BusinessException(ErrorCode.USER_ERROR);
//        }
//        if (hashUtils.DefaultVerify(body.getPassword(), user.getPassword_hash()) == true) {
//
//            String uuid32 = tokenUtils.getUUID32();
//            Long time = new Date().getTime();
//            Long exp = time + token_exp * 1000;
//            UserToken userToken = new UserToken(uuid32, exp);
//            TokenUser tokenUser = new TokenUser(user.getId(), exp);
//            String s_user_id = user.getId() + "";
//
//            redissonutils.lock(s_user_id);
//            redisUtils.setUserToken(s_user_id, userToken);
//            redisUtils.setTokenUser(uuid32, tokenUser);
//            redissonutils.unlock(s_user_id);
//
//            return new BaseResponse(200, new HttpLoginResponse(user.getId(), uuid32));
//        }
        throw new BusinessException(ErrorCode.PASSWORD_ERROR);
    }

    @Transactional
    @Override
    public BaseResponse sign(HttpSignRequest body) {
        String username = body.getUsername();
        String phone = body.getPhone();
        String password = body.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
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
