package com.xzccc.server.utils;

import com.xzccc.server.constant.RedisConstant;
import com.xzccc.server.model.Redis.TokenUser;
import com.xzccc.server.model.Redis.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisUtils {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    ValueOperations<String,Object> valueOperations;



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
        valueOperations.set(key,userToken);
        Long exp = userToken.getExp();
        if (exp!=null) {
            expireKeyAt(key,new Date(exp));
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
        valueOperations.set(key,tokenUser);
        Long exp = tokenUser.getExp();
        if (exp!=null) {
            expireKeyAt(key,new Date(exp));
        }

    }

    /**
     * 通过user_id获取token
     */
    public Object getTokenUser(String token){
        Object object = valueOperations.get(RedisConstant.UserToken + ":" + token);
        return object;
    }
}
