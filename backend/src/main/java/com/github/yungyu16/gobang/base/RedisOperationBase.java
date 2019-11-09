/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/8.
 */
public abstract class RedisOperationBase {

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
