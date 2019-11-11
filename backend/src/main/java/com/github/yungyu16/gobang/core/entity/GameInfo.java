package com.github.yungyu16.gobang.core.entity;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class GameInfo {
    private Integer gameId;
    private List<GamePartaker> gameWatchers = Lists.newCopyOnWriteArrayList();
    private GamePartaker blackUser;
    private GamePartaker whiteUser;
    //0 空 1 黑 2 白
    private int[][] board;
    private boolean startedFlag;

    public GameInfo(Integer gameId) {
        this.gameId = gameId;
    }

    public GamePartaker enterGame(UserRecord userRecord) {
        ConditionTools.checkNotNull(userRecord);
        Optional<GamePartaker> gamePartakerOpt = getGamePartaker(userRecord.getId());
        if (gamePartakerOpt.isPresent()) {
            return gamePartakerOpt.get();
        }
        GamePartaker newGamePartaker = new GamePartaker(userRecord.getId(), userRecord.getUserName());

        if (blackUser == null && whiteUser == null) {
            boolean flag = RandomUtils.nextBoolean();
            if (flag) {
                newGamePartaker.setGameRole(1);
                this.blackUser = newGamePartaker;
            } else {
                newGamePartaker.setGameRole(2);
                this.whiteUser = newGamePartaker;
            }
        } else if (blackUser == null || whiteUser == null) {
            if (blackUser == null) {
                newGamePartaker.setGameRole(1);
                this.blackUser = newGamePartaker;
            } else {
                newGamePartaker.setGameRole(2);
                this.whiteUser = newGamePartaker;
            }
        } else {
            newGamePartaker.setGameRole(3);
        }
        gameWatchers.add(newGamePartaker);
        return newGamePartaker;
    }

    public int userColor(Integer userId) {
        ConditionTools.checkNotNull(userId);
        if (this.blackUser != null && this.blackUser.is(userId)) {
            return 1;
        }
        if (this.whiteUser != null && this.whiteUser.is(userId)) {
            return 2;
        }
        return -1;
    }

    /**
     * @param userId
     * @return
     */
    public Optional<GamePartaker> getGamePartaker(Integer userId) {
        ConditionTools.checkNotNull(userId);
        if (blackUser != null && blackUser.is(userId)) {
            return Optional.of(blackUser);
        }
        if (whiteUser != null && whiteUser.is(userId)) {
            return Optional.of(whiteUser);
        }
        return gameWatchers.stream()
                .filter(it -> it.is(userId))
                .findAny();
    }

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
