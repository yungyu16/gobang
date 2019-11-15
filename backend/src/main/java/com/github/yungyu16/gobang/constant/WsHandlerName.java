package com.github.yungyu16.gobang.constant;

/**
 * CreatedDate: 2019/11/15
 * Author: songjialin
 */
public enum WsHandlerName {
    ONLINE_USER("user");
    private String path;

    WsHandlerName(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
