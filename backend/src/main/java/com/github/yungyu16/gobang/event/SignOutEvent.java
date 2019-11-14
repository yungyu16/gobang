package com.github.yungyu16.gobang.event;

import lombok.Getter;
import lombok.Setter;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
@Getter
@Setter
public class SignOutEvent extends BaseUserIdEvent {

    public SignOutEvent(Integer userId) {
        super(userId);
    }
}
