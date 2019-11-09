/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import cn.xiaoshidai.common.toolkit.base.ServletTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.github.yungyu16.gobang.model.ReqResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Slf4j
@ControllerAdvice
@ResponseBody
public class WebExceptionHandler {

    @ExceptionHandler
    public void bizException(BizException e) {
        log.info("请求错误 {}", e.getMessage());
        try {
            ServletTools.getCurrentResponse().sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (IOException e1) {
            log.error("异常处理异常...");
        }
    }

    @ExceptionHandler
    public void bizException(BizSessionTimeOutException e) {
        log.info("未登陆");
        try {
            ServletTools.getCurrentResponse().sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        } catch (IOException e1) {
            log.error("异常处理异常...");
        }
    }

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
