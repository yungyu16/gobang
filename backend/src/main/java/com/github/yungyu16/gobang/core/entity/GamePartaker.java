package com.github.yungyu16.gobang.core.entity;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
@Data
public class GamePartaker {


    private Integer gameId;

    private Integer userId;

    /**
     * 1 黑 2 白 3 观战
     */
    private Integer gameRole;

    private String userName;

    private WebSocketSession session;

    public GamePartaker(Integer userId, String userName, Integer gameId) {
        this.userId = userId;
        this.userName = userName;
        this.gameId = gameId;
    }

    public boolean isGameWatcher() {
        return gameRole == 3;
    }

    public boolean is(Integer userId) {
        Preconditions.checkNotNull(userId);
        return this.userId == userId;
    }
}
