/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.base;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import cn.xiaoshidai.common.toolkit.base.ServletTools;
import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/8.
 */
public abstract class SessionOperationBase extends RedisOperationBase {
    @Autowired
    private UserDomain userDomain;
    public final String USER_NAME = "userName";

    public final String USER_ID = "userId";

    protected Optional<UserRecord> getCurrentUserRecord() {
        return getSessionAttr(USER_ID).map(it -> {
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
        String token = getSessionToken().orElseThrow(BizSessionTimeOutException::new);
        return getSessionAttr(token, attrKey);
    }

    protected Optional<String> getSessionAttr(String sessionToken, String attrKey) {
        if (StringTools.isAnyBlank(sessionToken, attrKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getRedisHashOperations().get(sessionToken, attrKey));
    }

    protected void setSessionAttr(String attrKey, Object attrValue) {
        ConditionTools.checkNotNull(attrValue);
        String token = getSessionToken().orElseThrow(BizSessionTimeOutException::new);
        if (StringTools.isAnyBlank(token, attrKey)) {
            throw new NullPointerException();
        }
        getRedisHashOperations().put(token, attrKey, JSON.toJSONString(attrValue));
    }

    protected void deleteOldSession(String mobile) {
        if (StringTools.isBlank(mobile)) {
            return;
        }
        String oldToken = getRedisValueOperations().get("SESSION_HOLDER_" + mobile);
        if (StringTools.isBlank(oldToken)) {
            return;
        }
        redisTemplate.delete(oldToken);
    }

    protected String newSession(Integer userId, String mobile) {
        ConditionTools.checkNotNull(userId);
        String token = StringTools.timestampUUID();
        getRedisHashOperations().put(token, USER_ID, JSON.toJSONString(userId));
        getRedisValueOperations().set("SESSION_HOLDER_" + mobile, token);
        touchSession(token);
        return token;
    }

    protected void removeSession(String sessionToken) {
        ConditionTools.checkNotNull(sessionToken);
        redisTemplate.delete(sessionToken);
    }

    protected boolean checkSessionToken(String token) {
        if (StringTools.isBlank(token)) {
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
        if (StringTools.isBlank(token)) {
            return;
        }
        log.info("session有效期顺延7天");
        redisTemplate.expire(token, 7, TimeUnit.DAYS);
    }

    protected Optional<String> getSessionToken() {
        HttpServletRequest currentRequest = ServletTools.getCurrentRequest();
        return Optional.ofNullable(currentRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }
}
