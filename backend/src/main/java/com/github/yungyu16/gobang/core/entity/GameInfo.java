package com.github.yungyu16.gobang.core.entity;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private CheckPointInfo[][] board;

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
                checkPoints.put(String.format("x%s:y%s", i, j), pointInfo);
            }
        }
    }

    public boolean checkBoardPoint(int color, int x, int y) {
        if (color != 1 && color != 2) {
            return false;
        }
        CheckPointInfo pointInfo = checkPoints.get(String.format("x%s:y%s", x, y));
        if (pointInfo == null) {
            return false;
        }
        if (pointInfo.getColor() > 0) {
            return false;
        }
        pointInfo.setColor(color);
        checkBoardCount = checkBoardCount + 1;
        return true;
    }

    public List<CheckPointInfo> getCheckedPoints() {
        return checkPoints.values().stream().filter(it -> it.getColor() > 0).collect(Collectors.toList());
    }

    public GameInfo(Integer gameId) {
        this.gameId = gameId;
    }

    public GamePartaker enterGame(UserRecord userRecord) {
        ConditionTools.checkNotNull(userRecord);
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

    /**
     * @param userId
     *
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
     *
     * @return 0 无胜负 1 黑胜 2 白胜
     */
    public boolean isWinner(int color, int x, int y) {
        int count = 1;      //本身一点为 1
        int posX = 0;
        int posY = 0;
        /**判断水平方向上的胜负
         /* 将水平方向以传入的点x上的y轴作为分隔线分为两部分
         * 先向左边遍历，判断到的相同的连续的点  count++
         */
        for (posX = x - 1; posX > 0; posX--) {
            if (board[posX][y].getColor() == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                break;
            }
        }    //向右边遍历
        for (posX = x + 1; posX <= 15; posX++) {
            if (board[posX][y].getColor() == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                break;
            }
        }
        /**判断垂直方向上的胜负
         /* 将垂直方向以传入的点y上的x轴作为分隔线分为两部分
         * 先向上遍历，判断到的相同的连续的点  count++
         */
        for (posY = y - 1; posY > 0; posY--) {
            if (board[x][posY].getColor() == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                break;
            }
        }//向下遍历
        for (posY = y + 1; posY <= 15; posY++) {
            if (board[x][posY].getColor() == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                break;
            }
        }
        /**判断左上右下方向上的胜负
         * 以坐标点为分割线，将棋盘分为左右两个等腰三角形
         * 先判断左边的
         */
        for (posX = x - 1, posY = y - 1; posX > 0 && posY > 0; posX--, posY--) {
            if (board[posX][posY].getColor() == color) {
                count++;
                if (count >= 5) {
                    count = 1;
                    return true;
                }
            } else {
                break;
            }
        }//判断右边的
        for (posX = x + 1, posY = y + 1; posX <= 15 && posY <= 15; posX++, posY++) {
            if (board[posX][posY].getColor() == color) {
                count++;
                if (count >= 5) {
                    count = 1;
                    return true;
                }
            } else {
                break;
            }
        }
        /**判断右下左下方向上的胜负
         * 以坐标点为分割线，将棋盘分为左右两个等腰三角形
         * 先判断左边的
         */
        for (posX = x + 1, posY = y - 1; posX <= 15 && posY > 0; posX++, posY--) {
            if (board[posX][posY].getColor() == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                break;
            }
        }//判断右边的
        for (posX = x - 1, posY = y + 1; posX > 0 && posY <= 15; posX--, posY++) {
            if (board[posX][posY].getColor() == color) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }


}
