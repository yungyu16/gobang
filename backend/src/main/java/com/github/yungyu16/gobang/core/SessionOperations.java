package com.github.yungyu16.gobang.core;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public class SessionOperations {

    @Autowired
    private UserDomain userDomain;

    protected Optional<UserRecord> getCurrentUserRecord() {
        String token = getSessionToken().orElseThrow(BizSessionTimeoutException::new);
        return getCurrentUserRecord(token);
    }

    protected Optional<UserRecord> getCurrentUserRecord(String sessionToken) {
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

    protected Optional<Integer> getSessionUserId() {
        return getSessionAttr(USER_ID).map(it -> JSON.parseObject(it, Integer.class));
    }

    protected Optional<Integer> getSessionUserId(String sessionToken) {
        return getSessionAttr(sessionToken, USER_ID).map(it -> JSON.parseObject(it, Integer.class));
    }

    protected Optional<Integer> getSessionName() {
        return getSessionAttr(USER_NAME).map(it -> JSON.parseObject(it, Integer.class));
    }

    protected Optional<Integer> getSessionName(String sessionToken) {
        return getSessionAttr(sessionToken, USER_NAME).map(it -> JSON.parseObject(it, Integer.class));
    }

    protected Optional<String> getSessionAttr(String attrKey) {
        String token = getSessionToken().orElseThrow(BizSessionTimeoutException::new);
        return getSessionAttr(token, attrKey);
    }

    protected Optional<String> getSessionAttr(String sessionToken, String attrKey) {
        if (StringUtils.isAnyBlank(sessionToken, attrKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getRedisHashOperations().get(sessionToken, attrKey));
    }

    protected void setSessionAttr(String attrKey, Object attrValue) {
        Preconditions.checkNotNull(attrValue);
        String token = getSessionToken().orElseThrow(BizSessionTimeoutException::new);
        if (StringUtils.isAnyBlank(token, attrKey)) {
            throw new NullPointerException();
        }
        getRedisHashOperations().put(token, attrKey, JSON.toJSONString(attrValue));
    }

    protected void deleteOldSession(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return;
        }
        String oldToken = getRedisValueOperations().get("SESSION_HOLDER_" + mobile);
        if (StringUtils.isBlank(oldToken)) {
            return;
        }
        redisTemplate.delete(oldToken);
    }

    protected String newSession(Integer userId, String mobile) {
        Preconditions.checkNotNull(userId);
        String token = UUID.randomUUID().toString();
        getRedisHashOperations().put(token, USER_ID, JSON.toJSONString(userId));
        getRedisValueOperations().set("SESSION_HOLDER_" + mobile, token);
        touchSession(token);
        return token;
    }

    protected void removeSession(String sessionToken) {
        Preconditions.checkNotNull(sessionToken);
        redisTemplate.delete(sessionToken);
    }

    protected boolean checkSessionToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        token = token.trim();
        Boolean hasKey = redisTemplate.hasKey(token);
        if (hasKey == null) {
            hasKey = false;
        }
        if (hasKey) {
            touchSession(token);
        }
        return hasKey;
    }

    protected void touchSession(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        log.info("session有效期顺延7天");
        redisTemplate.expire(token, 7, TimeUnit.DAYS);
    }

    protected Optional<String> getSessionToken() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            return Optional.empty();
        }
        HttpServletRequest currentRequest = ra.getRequest();
        return Optional.ofNullable(currentRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }
}
