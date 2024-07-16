package com.sparta.sensorypeople.common.redisson;//package com.sparta.sensorypeople.common.config;


import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;


    public static final long WAIT_TIME = 10L;
    public static final long LEASE_TIME = 5L;
    public static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;

    private static final String REDISSON_HOST_PREFIX = "redis://";


    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
//                .setPassword("1234");

        return Redisson.create(config);
    }



}