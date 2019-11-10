/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import cn.xiaoshidai.common.toolkit.exception.BizException;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.model.ReqResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Aspect
@ControllerAdvice
public class DefaultControllerAdvice {

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Around("(controller() || restController())")
    public Object aroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        log.info("请求内容：{}", JSON.toJSONString(args, true));
        Object body = joinPoint.proceed();
        log.info("响应内容：{}", JSON.toJSONString(body, true));
        return body;
    }

    @ExceptionHandler
    @ResponseBody
    public ReqResult handleException(Exception e) {
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
