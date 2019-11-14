/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.github.yungyu16.gobang.base.WebRespBase;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
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

    private ValueFilter base64ValFilter = (object, name, value) -> {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            final String strVal = (String) value;
            int length = strVal.length();
            if (length > 100) {
                return strVal.substring(0, 20) + "...Length:[" + length + "]";
            }
            return strVal;
        }
        return value;
    };

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestBody)")
    public void requestBody() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ResponseBody)")
    public void responseBody() {
    }

    @Before(value = "requestBody()")
    public void beforeApi(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        List<Object> collectedArgs = Arrays.stream(args)
                .filter(it -> !(it instanceof ServletRequest))
                .filter(it -> !(it instanceof ServletResponse))
                .collect(Collectors.toList());
        String reqStr = "{}";
        if (!collectedArgs.isEmpty()) {
            reqStr = JSON.toJSONString(collectedArgs.get(0), base64ValFilter, SerializerFeature.PrettyFormat);
        }
        log.info("请求内容：{}", reqStr);

    }

    @AfterReturning(value = "(responseBody() || restController())", returning = "returnValue")
    public void afterApi(Object returnValue) {
        String respStr = "{}";
        if (!(returnValue instanceof ResponseEntity)) {
            respStr = JSON.toJSONString(returnValue, base64ValFilter, SerializerFeature.PrettyFormat);
        }
        log.info("响应内容：{}", respStr);
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
