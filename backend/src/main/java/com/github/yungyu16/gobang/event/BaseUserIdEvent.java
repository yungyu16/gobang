package com.github.yungyu16.gobang.event;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * CreatedDate: 2019/11/14
 * Author: songjialin
 */
@Getter
public abstract class BaseUserIdEvent extends ApplicationEvent {
    private Integer userId;

    public BaseUserIdEvent(Integer userId) {
        super(userId);
        Preconditions.checkNotNull(userId);
        this.userId = userId;
    }
}
