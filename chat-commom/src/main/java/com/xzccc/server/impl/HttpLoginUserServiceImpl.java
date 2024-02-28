package com.xzccc.server.impl;


import com.xzccc.common.BaseResponse;
import com.xzccc.common.ErrorCode;
import com.xzccc.concurrent.CallbackTask;
import com.xzccc.concurrent.CallbackTaskScheduler;
import com.xzccc.constant.RedisConstant;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@Slf4j
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

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String from;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.exp}")
    long mail_exp;
    @Autowired
    CallbackTaskScheduler callbackTaskScheduler;
    @Value("${limit.email.exp}")
    long limit_email_exp;

    @Override
    public BaseResponse login(HttpLoginRequest body) {
        String account = body.getAccount();
        String type = body.getType();
        if ("phone".equals(type)) {
            // 手机号登录模式
        } else if ("account".equals(type)) {
            // 账号登录模式
        }else
            throw new BusinessException(ErrorCode.LOGIN_TYPE_ERROR);
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
    public BaseResponse register(HttpSignRequest body) {
        String email = body.getEmail();
        if (StringUtils.isBlank(email) || !isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String code = body.getCode();
        redissonutils.lock(email+"register");
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String redis_code = stringStringValueOperations.get(RedisConstant.EmailCode + ":" + email);
        if (redis_code != null) {
            stringRedisTemplate.delete(RedisConstant.EmailCode + ":" + email);
        }
        redissonutils.unlock(email+"register");
        if (redis_code == null || !code.equals(redis_code)) {
            throw new BusinessException(ErrorCode.EMAIL_CODE_ERROR);
        }

        String account = body.getAccount();
        String username = body.getUsername();
        String password = body.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(account) || StringUtils.isBlank(password)
                || username.length() > 20 || account.length() < 3 || account.length() > 20
                || password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userMapper.select_by_email(email);
        if (user != null) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }
        user = userMapper.select_by_account(account);
        if (user != null) {
            throw new BusinessException(ErrorCode.ACCOUNT_EXISTS);
        }
        user = new User();
        user.setPassword_hash(hashUtils.DefaultHash(password));
        user.setEmail(email);
        user.setAccount(account);
        user.setUsername(username);
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return new BaseResponse(200, true);
    }

    @Override
    public void email_code(String email) {
        if (isValidEmail(email)) {
            redissonutils.lock(email+"email_code");
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            Boolean b = stringStringValueOperations.setIfAbsent(RedisConstant.EmailLimit + ":" + email, "1", limit_email_exp, TimeUnit.SECONDS);
            if (b == false) {
                throw new BusinessException(ErrorCode.EMAIL_LIMIT_ERROR);
            }
            redissonutils.unlock(email+"email_code");

            callbackTaskScheduler.add(new CallbackTask<Boolean>() {
                @Override
                public Boolean execute() throws Exception {
                    send_email(email);
                    return true;
                }

                @Override
                public void onBack(Boolean r) {
                    if (r) {
                        log.info("邮箱发送成功:" + email);
                    }
                }

                @Override
                public void onException(Throwable t) {
                    log.info("邮箱发送失败:" + email);
                }
            });
        } else
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    public boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    private String generateCode() {
        Long codeL = System.nanoTime();
        String codeStr = Long.toString(codeL);
        String verifyCode = codeStr.substring(codeStr.length() - 6);
        return verifyCode;
    }

    public void send_email(String email) {
        String code = generateCode();
        stringRedisTemplate.opsForValue().set(RedisConstant.EmailCode + ":" + email, code, mail_exp, TimeUnit.SECONDS);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("im邮箱验证码");
        simpleMailMessage.setText(code);
        javaMailSender.send(simpleMailMessage);
    }

}
