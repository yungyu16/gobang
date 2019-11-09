/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import cn.xiaoshidai.common.toolkit.exception.BizException;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.github.yungyu16.gobang.model.ReqResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
@ResponseBody
public class WebExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public ReqResult unknownException(Exception e) {
        if (e instanceof BizSessionTimeOutException) {
            log.info("会话过期 {}", e.getMessage());
            return ReqResult.sessionTimeout();
        }
        if (e instanceof BizException) {
            log.info("请求错误 {}", e.getMessage());
            return ReqResult.badRequest(e.getMessage());
        }
        log.error("服务端异常", e);
        return ReqResult.error();
    }
}
