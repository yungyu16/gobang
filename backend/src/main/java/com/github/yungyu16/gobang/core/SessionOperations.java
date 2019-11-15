package com.github.yungyu16.gobang.core;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.yungyu16.gobang.constant.SessionConstants.USER_ID;
import static com.github.yungyu16.gobang.constant.SessionConstants.USER_NAME;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public class SessionOperations extends LogOperationsBase {

    @Autowired
    private UserDomain userDomain;
    @Autowired
    private RedisOperations redisOperations;

    public Optional<UserRecord> getCurrentUserRecord() {
        String token = getSessionToken().orElseThrow(BizSessionTimeoutException::new);
        return getCurrentUserRecord(token);
    }

    public Optional<UserRecord> getCurrentUserRecord(String sessionToken) {
        if (StringUtils.isBlank(sessionToken)) {
            return Optional.empty();
        }
        return getSessionAttr(sessionToken, USER_ID).map(it -> {
            UserRecord byId = userDomain.getById(it);
            if (byId.getIsDeleted()) {
                return null;
            }
            return byId;
        });
    }

    public Optional<Integer> getSessionUserId() {
        return getSessionAttr(USER_ID).map(it -> JSON.parseObject(it, Integer.class));
    }

    public Optional<Integer> getSessionUserId(String sessionToken) {
        return getSessionAttr(sessionToken, USER_ID).map(it -> JSON.parseObject(it, Integer.class));
    }

    public Optional<Integer> getSessionName() {
        return getSessionAttr(USER_NAME).map(it -> JSON.parseObject(it, Integer.class));
    }

    public Optional<Integer> getSessionName(String sessionToken) {
        return getSessionAttr(sessionToken, USER_NAME).map(it -> JSON.parseObject(it, Integer.class));
    }

    public Optional<String> getSessionAttr(String attrKey) {
        String token = getSessionToken().orElseThrow(BizSessionTimeoutException::new);
        return getSessionAttr(token, attrKey);
    }

    public Optional<String> getSessionAttr(String sessionToken, String attrKey) {
        if (StringUtils.isAnyBlank(sessionToken, attrKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(redisOperations.getRedisHashOperations().get(sessionToken, attrKey));
    }

    public void setSessionAttr(String attrKey, Object attrValue) {
        Preconditions.checkNotNull(attrValue);
        String token = getSessionToken().orElseThrow(BizSessionTimeoutException::new);
        if (StringUtils.isAnyBlank(token, attrKey)) {
            throw new NullPointerException();
        }
        redisOperations.getRedisHashOperations().put(token, attrKey, JSON.toJSONString(attrValue));
    }

    public String newSession(Integer userId, String mobile) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(mobile);
        mobile = mobile.trim();

        String SESSION_HOLDER_KEY = "SESSION_HOLDER_" + mobile;
        String oldToken = redisOperations.getRedisValueOperations().get(SESSION_HOLDER_KEY);
        if (!StringUtils.isBlank(oldToken)) {
            redisOperations.getRedisTemplate().delete(oldToken);
        }

        String token = UUID.randomUUID().toString();
        redisOperations.getRedisHashOperations().put(token, USER_ID, JSON.toJSONString(userId));
        redisOperations.getRedisValueOperations().set(SESSION_HOLDER_KEY, token);
        touchSession(token);
        return token;
    }

    public void removeSession(String sessionToken) {
        Preconditions.checkNotNull(sessionToken);
        redisOperations.getRedisTemplate().delete(sessionToken);
    }

    public boolean checkSessionToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        token = token.trim();
        Boolean hasKey = redisOperations.getRedisTemplate().hasKey(token);
        if (hasKey == null) {
            hasKey = false;
        }
        if (hasKey) {
            touchSession(token);
        }
        return hasKey;
    }

    public void touchSession(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        log.info("session有效期顺延7天");
        redisOperations.getRedisTemplate().expire(token, 7, TimeUnit.DAYS);
    }

    public Optional<String> getSessionToken() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            return Optional.empty();
        }
        HttpServletRequest currentRequest = ra.getRequest();
        return Optional.ofNullable(currentRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }
}
