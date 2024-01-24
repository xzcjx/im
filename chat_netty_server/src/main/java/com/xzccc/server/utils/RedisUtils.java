package com.xzccc.server.utils;

import com.xzccc.server.config.RedisConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisUtils extends RedisConfig {

    private static RedisTemplate<String, Object> redisTemplate;
    private static void init(){
        RedisUtils redisUtils = new RedisUtils();
        redisTemplate = redisUtils.redisTemplate(new LettuceConnectionFactory());
    }

    public static ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }
}
