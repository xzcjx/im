package com.xzccc.loginStrategy;

import com.xzccc.common.ErrorCode;
import com.xzccc.exception.BusinessException;
import com.xzccc.mapper.UserMapper;
import com.xzccc.model.Dao.User;
import com.xzccc.model.Redis.TokenUser;
import com.xzccc.model.Redis.UserToken;
import com.xzccc.model.request.HttpLoginRequest;
import com.xzccc.utils.HashUtils;
import com.xzccc.utils.RedisUtils;
import com.xzccc.utils.RedissonUtils;
import com.xzccc.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AbstractLoginTemplate implements ILogin {
    @Autowired
    UserMapper userMapper;
    @Autowired
    HashUtils hashUtils;
    @Autowired
    TokenUtils tokenUtils;
    @Value("${token.exp}")
    long token_exp;
    @Autowired
    RedissonUtils redissonUtils;
    @Autowired
    RedisUtils redisUtils;

    @Override
    public void check(String account, String password) {
    }


    @Override
    public User verify(String account, String password) {
        User user = get_user(account);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_ERROR);
        }
        String password_hash = hashUtils.DefaultHash(password);
        if (!password_hash.equals(user.getPassword_hash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        return user;
    }

    @Override
    public String create_token(User user) {
        String uuid32 = tokenUtils.getUUID32();
        Long time = new Date().getTime();
        Long exp = time + token_exp * 1000;
        UserToken userToken = new UserToken(uuid32, exp);
        TokenUser tokenUser = new TokenUser(user.getId(), exp);
        String s_user_id = user.getId() + "";
        redissonUtils.lock(user.getId() + "login");
        redisUtils.setUserToken(s_user_id, userToken);
        redisUtils.setTokenUser(uuid32, tokenUser);
        redissonUtils.unlock(s_user_id + "login");
        return uuid32;
    }

    @Override
    public User get_user(String account) {
        return null;
    }

    public String login(HttpLoginRequest httpLoginRequest) {
        String account = httpLoginRequest.getAccount();
        String password = httpLoginRequest.getPassword();
        check(account, password);
        User user = verify(account, password);
        String token = create_token(user);
        return token;
    }
}
