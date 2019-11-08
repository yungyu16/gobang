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

    private int bizCode = 0;

    private String bizMsg;

    private T data;

    private ReqResult(int bizCode, String bizMsg, T data) {
        this.bizCode = bizCode;
        this.bizMsg = bizMsg;
        this.data = data;
    }

    public static <T> ReqResult<T> success() {
        return new ReqResult<>(0, null, null);
    }

    public static <T> ReqResult<T> success(T data) {
        return new ReqResult<>(0, null, data);
    }

    public static <T> ReqResult<T> of(int bizCode) {
        return new ReqResult<>(bizCode, null, null);
    }

    public static <T> ReqResult<T> of(int bizCode, String bizMsg) {
        return new ReqResult<>(bizCode, bizMsg, null);
    }
}
