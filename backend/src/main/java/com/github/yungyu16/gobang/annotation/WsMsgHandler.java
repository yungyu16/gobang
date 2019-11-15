package com.github.yungyu16.gobang.annotation;

import com.github.yungyu16.gobang.constant.WsHandlerName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CreatedDate: 2019/11/15
 * Author: songjialin
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WsMsgHandler {
    WsHandlerName handlerName();

    String msgType();

    String msgSubType();
}
