/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import cn.xiaoshidai.common.toolkit.base.ServletTools;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/8.
 */
public abstract class BaseController {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected Optional<String> getCurrentUserToken() {
        HttpServletRequest currentRequest = ServletTools.getCurrentRequest();
        String token = null;
        try {
            token = ServletRequestUtils.getStringParameter(currentRequest, "token");
        } catch (ServletRequestBindingException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(token);
    }

    protected String getCurrentUserTokenOrException() {
        return getCurrentUserToken().orElseThrow(BizSessionTimeOutException::new);
    }
}
