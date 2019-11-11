package com.github.yungyu16.gobang.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
@Getter
@Setter
public class SessionTokenEvent extends ApplicationEvent {
    public static final String TYPE_NEW = "new";
    public static final String TYPE_REMOVE = "remove";
    private String type;
    private String token;

    public SessionTokenEvent(String type, String token) {
        super(token);
        this.type = type;
        this.token = token;
    }
}
