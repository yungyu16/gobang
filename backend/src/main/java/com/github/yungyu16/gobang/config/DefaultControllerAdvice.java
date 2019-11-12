/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;


import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import com.github.yungyu16.gobang.base.WebRespBase;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Object> collectedArgs = Arrays.stream(args)
                .filter(it -> !(it instanceof ServletRequest))
                .filter(it -> !(it instanceof ServletResponse))
                .collect(Collectors.toList());
        log.info("请求内容：{}", JSON.toJSONString(collectedArgs, true));
        Object body = joinPoint.proceed();
        log.info("响应内容：{}", JSON.toJSONString(body, true));
        return body;
    }

    @ExceptionHandler
    @ResponseBody
    public WebRespBase handleException(Exception e) {
        if (e instanceof BizSessionTimeoutException) {
            log.info("会话过期 {}", e.getMessage());
            return WebRespBase.sessionTimeout();
        }
        if (e instanceof BizException) {
            log.info("请求错误 {}", e.getMessage());
            return WebRespBase.badRequest(e.getMessage());
        }
        log.error("服务端异常", e);
        return WebRespBase.error();
    }
}
