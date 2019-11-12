package com.github.yungyu16.gobang.core.entity;

import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class GameInfo {

    public static final int GAME_ROLE_BLACK = 1;

    public static final int GAME_ROLE_WHITE = 2;

    public static final int GAME_ROLE_WATCHER = 3;

    private Integer gameId;

    private List<GamePartaker> gameWatchers = Lists.newCopyOnWriteArrayList();

    private GamePartaker blackUser;

    private GamePartaker whiteUser;

    //0 空 1 黑 2 白
    private CheckPointInfo[][] board = new CheckPointInfo[15][15];

    private Map<String, CheckPointInfo> checkPoints = Maps.newHashMap();

    //-1 未开始 0 已开始 1 已经结束
    private int gameStatus = -1;

    private int checkBoardCount = 0;

    private int latestCheckColor = 2;

    {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                CheckPointInfo pointInfo = new CheckPointInfo(i, j);
                board[i][j] = pointInfo;
                checkPoints.put(pointId(i, j), pointInfo);
            }
        }
    }

    public GameInfo(Integer gameId) {
        this.gameId = gameId;
    }

    public boolean isGameOver() {
        return this.gameStatus == 1;
    }

    public boolean checkBoardPoint(int color, int x, int y) {
        if (color != 1 && color != 2) {
            return false;
        }
        if (color == this.latestCheckColor) {
            return false;
        }
        CheckPointInfo pointInfo = checkPoints.get(pointId(x, y));
        if (pointInfo == null) {
            return false;
        }
        if (pointInfo.getColor() > 0) {
            return false;
        }
        pointInfo.setColor(color);
        this.checkBoardCount = checkBoardCount + 1;
        this.latestCheckColor = color;
        return true;
    }

    public List<CheckPointInfo> getCheckedPoints() {
        return checkPoints.values().stream().filter(it -> it.getColor() > 0).collect(Collectors.toList());
    }

    public GamePartaker enterGame(UserRecord userRecord) {
        Preconditions.checkNotNull(userRecord);
        Optional<GamePartaker> gamePartakerOpt = getGamePartaker(userRecord.getId());
        if (gamePartakerOpt.isPresent()) {
            return gamePartakerOpt.get();
        }
        GamePartaker newGamePartaker = new GamePartaker(userRecord.getId(), userRecord.getUserName(), this.gameId);

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
                newGamePartaker.setGameRole(GAME_ROLE_BLACK);
                this.blackUser = newGamePartaker;
            } else {
                newGamePartaker.setGameRole(GAME_ROLE_WHITE);
                this.whiteUser = newGamePartaker;
            }
        } else {
            newGamePartaker.setGameRole(GAME_ROLE_WATCHER);
        }
        gameWatchers.add(newGamePartaker);
        return newGamePartaker;
    }

    /**
     * @param userId
     *
     * @return
     */
    public Optional<GamePartaker> getGamePartaker(Integer userId) {
        Preconditions.checkNotNull(userId);
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
     *
     * @return 0 无胜负 1 黑胜 2 白胜
     */
    public boolean isWinner(int color, int x, int y) {
        //横
        if (is5PointLine(color, d -> x - d, d -> y, d -> x + d, d -> y)) {
            return true;
        }
        //竖
        if (is5PointLine(color, d -> x, d -> y - d, d -> x, d -> y + d)) {
            return true;
        }
        //左斜
        if (is5PointLine(color, d -> x - d, d -> y + d, d -> x + d, d -> y + d)) {
            return true;
        }
        //右斜
        return is5PointLine(color, d -> x - d, d -> y + d, d -> x + d, d -> y - d);
    }

    private boolean is5PointLine(int color, IntFunction<Integer> leftXMapper, IntFunction<Integer> leftYMapper,
                                 IntFunction<Integer> rightXMapper, IntFunction<Integer> rightYMapper) {
        //左
        int leftLineCount = lineCount(color, leftXMapper, leftYMapper);
        if (leftLineCount == 4) {
            return true;
        }
        //右
        int rightLineCount = lineCount(color, rightXMapper, rightYMapper);
        if (rightLineCount == 4) {
            return true;
        }
        return leftLineCount + rightLineCount + 1 == 5;
    }

    private int lineCount(int targetColor, IntFunction<Integer> xMapper, IntFunction<Integer> yMapper) {
        int maxDistance = 4;
        for (int i = 1; i <= maxDistance; i++) {
            Integer thisX = xMapper.apply(i);
            Integer thisY = yMapper.apply(i);
            Preconditions.checkNotNull(thisX);
            Preconditions.checkNotNull(thisY);
            CheckPointInfo checkedPoint = getCheckedPoint(thisX, thisY);
            if (checkedPoint == null
                    || checkedPoint.getColor() != targetColor) {
                return i - 1;
            }
        }
        return 4;
    }

    private CheckPointInfo getCheckedPoint(int x, int y) {
        String pointId = pointId(x, y);
        return checkPoints.get(pointId);
    }

    private String pointId(int x, int y) {
        return String.format("x%s:y%s", x, y);
    }
}

