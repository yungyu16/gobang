package com.github.yungyu16.gobang.event;

import lombok.Getter;
import lombok.Setter;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
@Getter
@Setter
public class HeartBeatEvent extends BaseUserIdEvent {

    public HeartBeatEvent(Integer userId) {
        super(userId);
    }
}
