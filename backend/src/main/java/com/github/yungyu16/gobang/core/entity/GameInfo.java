package com.github.yungyu16.gobang.core.entity;

import com.github.yungyu16.gobang.core.game.*;
import com.github.yungyu16.gobang.core.game.referee.SimpleGameReferee;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.exeception.BizException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class GameInfo {
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private Integer gameId;

    private List<GamePartaker> gameWatchers = Lists.newCopyOnWriteArrayList();

    private GamePartaker blackUser;

    private GamePartaker whiteUser;

    private GobangGame game = new GobangGameImpl(15,new SimpleGameReferee(),null);

    //-1 未开始 0 已开始 1 已经结束
    private int gameStatus = -1;


    public GameInfo(Integer gameId) {
        this.gameId = gameId;
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

    public boolean checkBoardPoint(int color, int x, int y) {
        if (color != BLACK && color != WHITE) {
            return false;
        }
        return game.move(Board.Color.valueOf(color),x,y);
    }

    public List<CheckPointInfo> getCheckedPoints() {
//        return checkPoints.values().stream().filter(it -> it.getColor() > 0).collect(Collectors.toList());
        return game.getBoard().getTrace().stream().map(trace->
            new CheckPointInfo(trace.getX(),trace.getY(),trace.getColor().getValue())).collect(Collectors.toList());
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

    public int getLatestCheckColor(){
        PathNode latest = game.getBoard().getLatest();
        if (latest != null) {
            return latest.getColor().getValue();
        }else {
            // 黑棋现行，所以初始的上一子就是白棋
            return Board.Color.White.getValue();
        }
    }

    /**
     * @param color 正在下的棋子颜色 1 黑 2 白
     *
     * @return 0 无胜负 1 黑胜 2 白胜
     */
    public boolean isWinner(int color) {
        switch (color) {
            case 0:
                return false;
            case BLACK:
                return Winner.Black.equals(game.getWinner());
            case WHITE:
                return Winner.White.equals(game.getWinner());
        }
        throw new BizException("非法的棋子");
    }
}
