/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.model;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
public class ReqResult<T> {

    private int code = 0;

    private String errorMsg;

    private T data;

    private ReqResult(int code, String errorMsg, T data) {
        this.code = code;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public static <T> ReqResult<T> of(T data) {
        return new ReqResult<>(0, null, data);
    }

    public static <T> ReqResult<T> of(int code, String msg) {
        return new ReqResult<>(code, msg, null);
    }

    public static <T> ReqResult<T> of(int code, String msg, T data) {
        return new ReqResult<>(code, msg, data);
    }
}
