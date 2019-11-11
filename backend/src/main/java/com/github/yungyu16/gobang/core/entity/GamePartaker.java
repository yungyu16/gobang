package com.github.yungyu16.gobang.core.entity;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
@Data
public class GamePartaker {
    /**
     * 1 黑 2 白 3 观战
     */
    private Integer gameRole;
    private Integer userId;
    private String userName;
    private WebSocketSession session;

    public GamePartaker(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public boolean isGameWatcher() {
        return gameRole == 3;
    }

    public boolean is(Integer userId) {
        ConditionTools.checkNotNull(userId);
        return this.userId == userId;
    }
}
