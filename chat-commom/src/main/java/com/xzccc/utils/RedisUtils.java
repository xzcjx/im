package com.xzccc.utils;


import com.xzccc.constant.RedisConstant;
import com.xzccc.model.Redis.TokenUser;
import com.xzccc.model.Redis.UserToken;
import com.xzccc.server.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisUtils {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    ValueOperations<String,Object> valueOperations;

    @Autowired
    AccountService accountService;

    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public void persistKey(String key) {
        redisTemplate.persist(key);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 将设置user_id: token
     */
    public void setUserToken(String user_id, UserToken userToken) {
        String key = RedisConstant.UserToken + ":" + user_id;
        Boolean ifAbsent = valueOperations.setIfAbsent(key, userToken);
        if (ifAbsent == false) {
            accountService.kick_user(user_id);
            valueOperations.setIfAbsent(key, userToken);
        }
        Long exp = userToken.getExp();
        if (exp != null) {
            expireKeyAt(key, new Date(exp));
        }
    }

    /**
     * 通过user_id获取token
     */
    public Object getUserToken(String user_id){
        Object object = valueOperations.get(RedisConstant.UserToken + ":" + user_id);
        return object;
    }

    /**
     * 将设置user_id: token
     */
    public void setTokenUser(String token, TokenUser tokenUser) {
        String key=RedisConstant.TokenUser+":"+token;
        valueOperations.setIfAbsent(key,tokenUser);
        Long exp = tokenUser.getExp();
        if (exp!=null) {
            expireKeyAt(key,new Date(exp));
        }

    }

    /**
     * 通过user_id获取token
     */
    public TokenUser getTokenUser(String token){
        TokenUser tokenUser = (TokenUser) valueOperations.get(RedisConstant.TokenUser + ":" + token);
        return tokenUser;
    }

    public Boolean setUserOnline(Long userId){
        Boolean res = valueOperations.setBit(RedisConstant.UserStatus, userId, true);
        long statusCount = getStatusCount();
        log.info("im在线 "+statusCount+" 人");
        return res;
    }

    public Boolean setUserOffline(Long userId){
        Boolean res = valueOperations.setBit(RedisConstant.UserStatus, userId, false);
        long statusCount = getStatusCount();
        log.info("im在线 "+statusCount+" 人");
        return res;
    }
    
    public long getStatusCount(){
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(RedisConstant.UserStatus.getBytes());
            }
        });
    }

    public Boolean containerUserStatus(long offset){
        return container(RedisConstant.UserStatus,offset);
    }

    public Boolean container(String key, long offset) {
        return valueOperations.getBit(key, offset);
    }
}
