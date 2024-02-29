package com.xzccc.server.impl;


import com.xzccc.common.BaseResponse;
import com.xzccc.common.ErrorCode;
import com.xzccc.concurrent.CallbackTask;
import com.xzccc.concurrent.CallbackTaskScheduler;
import com.xzccc.constant.LoginConstant;
import com.xzccc.constant.RedisConstant;
import com.xzccc.exception.BusinessException;
import com.xzccc.loginStrategy.LoginStrategy;
import com.xzccc.mapper.UserMapper;
import com.xzccc.model.Dao.User;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


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
    RedissonUtils redissonUtils;


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
    @Autowired
    LoginStrategy loginStrategy;

    @Override
    public BaseResponse login(HttpLoginRequest body) {
        String account = body.getAccount();
        String type;
        if (isValidEmail(account)) {
            type = LoginConstant.EMAIL_TYPE;
        } else
            type = LoginConstant.ACCOUNT_TYPE;
        String token = loginStrategy.login(body, type);
        return new BaseResponse(200, token);
    }

    @Transactional
    @Override
    public BaseResponse register(HttpSignRequest body) {
        String email = body.getEmail();
        if (StringUtils.isBlank(email) || !isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String code = body.getCode();
        redissonUtils.lock(email + "register");
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String redis_code = stringStringValueOperations.get(RedisConstant.EmailCode + ":" + email);
        if (redis_code != null) {
            stringRedisTemplate.delete(RedisConstant.EmailCode + ":" + email);
        }
        redissonUtils.unlock(email + "register");
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
            redissonUtils.lock(email + "email_code");
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            Boolean b = stringStringValueOperations.setIfAbsent(RedisConstant.EmailLimit + ":" + email, "1", limit_email_exp, TimeUnit.SECONDS);
            if (b == false) {
                throw new BusinessException(ErrorCode.EMAIL_LIMIT_ERROR);
            }
            redissonUtils.unlock(email + "email_code");

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
