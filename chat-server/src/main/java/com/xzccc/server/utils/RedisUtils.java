package com.xzccc.server.utils;

import com.xzccc.server.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtils {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    private HashOperations<String, String, Object> stringObjectObjectHashOperations;


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
     * 将设置user_id: token
     */
    public void setUserToken(String user_id,String token) {
        stringObjectObjectHashOperations = redisTemplate.opsForHash();
        stringObjectObjectHashOperations.put(RedisConstant.AccountToken,user_id,token);
    }

    /**
     * 通过user_id获取token
     */
    public String getUserToken(String user_id){
        String token = (String) stringObjectObjectHashOperations.get(RedisConstant.AccountToken, user_id);
        return token;
    }

    /**
     * 将设置user_id: token
     */
    public void setTokenUser(String user_id,String token) {
        stringObjectObjectHashOperations = redisTemplate.opsForHash();
        stringObjectObjectHashOperations.put(RedisConstant.TokenAccount,token,user_id);
    }

    /**
     * 通过user_id获取token
     */
    public Long getTokenUser(String token){
        Long user_id= (Long) stringObjectObjectHashOperations.get(RedisConstant.TokenAccount,token);
        return user_id;
    }
}
