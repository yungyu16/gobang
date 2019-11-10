/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import cn.xiaoshidai.common.toolkit.exception.BizException;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.model.ReqResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
@ControllerAdvice
@ResponseBody
public class DefaultControllerAdvice implements ResponseBodyAdvice, RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            body = ReqResult.success();
        } else {
            if (!(body instanceof ReqResult)) {
                body = ReqResult.success(body);
            }
        }
        log.info("请求内容：{}", JSON.toJSONString(body, true));
        return body;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("响应内容：{}", JSON.toJSONString(body, true));
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
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
