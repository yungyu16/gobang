package com.github.yungyu16.gobang.web.websocket.msg;

import lombok.Data;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
@Data
public class GameInputMsg extends UserInputMsg {

    /**
     * 进入游戏
     */

    private Integer gameId;
}
