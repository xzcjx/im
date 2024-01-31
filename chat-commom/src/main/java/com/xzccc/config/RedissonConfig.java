package com.xzccc.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.redisson_database}")
    private int database;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient createRedissonClient(){
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(String.format("redis://%s:%s", host,port));
//        singleServerConfig.setPassword("")
        singleServerConfig.setDatabase(database);
        return Redisson.create(config);
    }
}
