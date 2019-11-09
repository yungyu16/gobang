/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.base;

import cn.xiaoshidai.common.toolkit.base.StringTools;

import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/8.
 */
public abstract class SessionOperationBase extends RedisOperationBase {

    public final String USER_NAME = "userName";

    protected Optional<String> getSessionAttr(String token, String attrKey) {
        if (StringTools.isAnyBlank(token, attrKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getRedisHashOperations().get(token, attrKey));
    }

    protected void setSessionAttr(String token, String attrKey, String attrValue) {
        if (StringTools.isAnyBlank(token, attrKey, attrValue)) {
            throw new NullPointerException();
        }
        getRedisHashOperations().put(token, attrKey, attrValue);
    }
}
