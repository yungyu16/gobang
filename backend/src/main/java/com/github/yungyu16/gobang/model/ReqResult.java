/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.model;

import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@Data
public class ReqResult<T> {

    private int code;

    private String msg;

    private T data;

    private ReqResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ReqResult<T> error() {
        return new ReqResult<>(500, "服务繁忙", null);
    }

    public static <T> ReqResult<T> badRequest(String msg) {
        return new ReqResult<>(400, msg, null);
    }

    public static <T> ReqResult<T> success() {
        return new ReqResult<>(200, null, null);
    }

    public static <T> ReqResult<T> success(T data) {
        return new ReqResult<>(200, null, data);
    }

    public static <T> ReqResult<T> sessionTimeout() {
        return new ReqResult<>(401, "会话过期", null);
    }
}
