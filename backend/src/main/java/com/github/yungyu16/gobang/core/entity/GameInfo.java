package com.github.yungyu16.gobang.core.entity;

import com.google.common.collect.Lists;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
public class GameInfo {
    private Integer gameId;
    private List<WebSocketSession> watchers = Lists.newCopyOnWriteArrayList();
    private WebSocketSession blackSession;
    private WebSocketSession whiteSession;
    private List<ChessmanInfo> checkedChessman = Lists.newCopyOnWriteArrayList();


    //0 空 1 黑 2 白
    private int[][] board;

    /**
     * @param color 正在下的棋子颜色 1 黑 2 白
     * @param x     x索引 0开始
     * @param y     y 索引 0开始
     * @return 0 无胜负 1 黑胜 2 白胜
     */
    public int predict(int color, int x, int y) {
        return 0;
    }
}
