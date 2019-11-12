/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.base;

import lombok.Data;
import org.slf4j.MDC;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@Data
public class WebRespBase<T> {

    private int code;

    private String traceId;

    private String msg;

    private T data;

    private WebRespBase(int code, String msg, T data) {
        this.traceId = MDC.get("traceId");
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> WebRespBase<T> error() {
        return new WebRespBase<>(500, "服务繁忙", null);
    }

    public static <T> WebRespBase<T> badRequest(String msg) {
        return new WebRespBase<>(400, msg, null);
    }

    public static <T> WebRespBase<T> success() {
        return new WebRespBase<>(200, null, null);
    }

    public static <T> WebRespBase<T> success(T data) {
        return new WebRespBase<>(200, null, data);
    }

    public static <T> WebRespBase<T> sessionTimeout() {
        return new WebRespBase<>(401, "会话过期,请重新登陆", null);
    }
}
