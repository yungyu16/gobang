package com.github.yungyu16.gobang.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public class RedisOperations {

    @Autowired
    protected StringRedisTemplate redisTemplate;

    protected ValueOperations<String, String> getRedisValueOperations() {
        return redisTemplate.opsForValue();
    }

    protected SetOperations<String, String> getRedisSetOperations() {
        return redisTemplate.opsForSet();
    }

    protected HashOperations<String, String, String> getRedisHashOperations() {
        return redisTemplate.opsForHash();
    }

}
