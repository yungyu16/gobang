package com.github.yungyu16.gobang.core.game.gobang;

import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.core.game.gobang.entity.GameInfo;
import com.github.yungyu16.gobang.core.game.gobang.entity.GamePartaker;
import com.github.yungyu16.gobang.dao.entity.GameRecord;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.GameDomain;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.ws.msg.OutputMsg;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Component
public class GobangContext extends LogOperationsBase implements InitializingBean {

    @Autowired
    private GameDomain gameDomain;

    private Map<Integer, GameInfo> onlineGames = Maps.newConcurrentMap();

    private Map<Integer, LocalDateTime> activeGames = Maps.newConcurrentMap();

    private Map<Integer, GamePartaker> userGames = Maps.newConcurrentMap();

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("refresh-game-th-%s").build());

    @Override
    public void afterPropertiesSet() {
        executorService.scheduleAtFixedRate(this::refreshActiveGame, 0, 5, TimeUnit.MINUTES);
    }

    private void refreshActiveGame() {
        log.info("开始刷新对局列表...");
        LocalDateTime now = LocalDateTime.now();
        activeGames.forEach((gameId, value) -> {
            long minutes = Duration.between(now, value).abs().toMinutes();
            if (minutes >= 5) {
                clearGame(gameId);
            }
        });
    }

    public void newGame(Integer gameId) {
        Preconditions.checkNotNull(gameId);
        onlineGames.computeIfAbsent(gameId, key -> new GameInfo(gameId));
    }

    /**
     * @return -1 无对战 0 观战中 1 对战中
     */
    public Optional<GamePartaker> userGame(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        GamePartaker gamePartaker = userGames.get(userId);
        if (gamePartaker == null) {
            return Optional.empty();
        }
        Integer gameId = gamePartaker.getGameId();
        if (gameId == null) {
            return Optional.empty();
        }
        GameInfo gameInfo = onlineGames.get(gameId);
        if (gameInfo.isGameOver()) {
            return Optional.empty();
        }
        return Optional.of(gamePartaker);
    }

    public synchronized void enterGame(WebSocketSession session, String sessionToken, Integer gameId) throws IOException {
        GameInfo gameInfo = onlineGames.get(gameId);
        if (gameInfo == null) {
            throw new BizException("对局已结束或不存在...");
        }
        touchGame(gameId);
        if (gameInfo.isGameOver()) {
            throw new BizException("对局已结束...");
        }
        UserRecord userRecord = getCurrentUserRecord(sessionToken).orElseThrow(() -> new BizException("用户未登录"));
        if (gameId == null) {
            throw new BizException("对局已结束或不存在...");
        }

        GamePartaker gamePartaker = gameInfo.enterGame(userRecord);
        log.info("当前用户角色为：{}", gamePartaker.getGameRole());
        gamePartaker.setSession(session);

        Integer userId = userRecord.getId();
        userGames.put(userId, gamePartaker);

        boolean isGameWatcher = gamePartaker.isGameWatcher();

        GamePartaker blackUser = gameInfo.getBlackUser();
        GamePartaker whiteUser = gameInfo.getWhiteUser();

        WebSocketSession gamePartakerSession = gamePartaker.getSession();
        gameInfo.getCheckedPoints()
                .forEach(it -> {
                    JSONObject checkPoint = new JSONObject();
                    checkPoint.put("color", it.getColor());
                    checkPoint.put("x", it.getX());
                    checkPoint.put("y", it.getY());
                    sendMsg(gamePartakerSession, OutputMsg.of(MsgTypes.GAME_MSG_CHECK_BOARD, checkPoint).toTextMessage());
                });

        if (isGameWatcher) {
            log.info("当前用户为观战用户：{}", gamePartaker.getUserName());
            OutputMsg<JSONObject> initMsg = newGameInitMsg(gamePartaker, blackUser, whiteUser, true);
            sendMsg(gamePartakerSession, initMsg.toTextMessage());

        } else {
            if (blackUser != null) {
                log.info("当前黑方已经有人：{}", blackUser.getUserName());
                OutputMsg<JSONObject> initMsg = newGameInitMsg(blackUser, blackUser, whiteUser, false);
                sendMsg(blackUser.getSession(), initMsg.toTextMessage());
            }
            if (whiteUser != null) {
                log.info("当前白方已经有人：{}", whiteUser.getUserName());
                OutputMsg<JSONObject> initMsg = newGameInitMsg(whiteUser, blackUser, whiteUser, false);
                sendMsg(whiteUser.getSession(), initMsg.toTextMessage());

            }
            if (blackUser != null && whiteUser != null) {
                log.info("当前双方都已就绪：{}", blackUser.getUserName());
                int gameStatus = gameInfo.getGameStatus();
                if (gameStatus == -1) {
                    gameInfo.setGameStatus(0);
                    GameRecord entity = new GameRecord();
                    entity.setId(gameId);
                    entity.setGameStartTime(LocalDateTime.now());
                    entity.setBlackUserId(blackUser.getUserId());
                    entity.setWhiteUserId(whiteUser.getUserId());
                    gameDomain.updateById(entity);
                }
                JSONObject startMsg = new JSONObject();
                startMsg.put("startColor", gameInfo.getLatestCheckColor());
                TextMessage msg = OutputMsg.of(MsgTypes.GAME_MSG_START_GAME, startMsg).toTextMessage();
                sendMsg(blackUser.getSession(), msg);
                sendMsg(whiteUser.getSession(), msg);
            }
            if (blackUser == null && whiteUser == null) {
                log.info("错误的状态...当前用户为：{}", gamePartaker.getUserName());
            }
        }
    }

    public synchronized void checkBoardPoint(String sessionToken, Integer gameId, JSONObject point) {
        UserRecord userRecord = getCurrentUserRecord(sessionToken).orElseThrow(() -> new BizException("用户未登录"));
        if (gameId == null) {
            throw new BizException("对局已结束或不存在...");
        }
        touchGame(gameId);
        Integer userId = userRecord.getId();
        GameInfo gameInfo = onlineGames.get(gameId);
        if (gameInfo == null) {
            throw new BizException("对局已结束或不存在...");
        }

        GamePartaker gamePartaker = gameInfo.getGamePartaker(userId).orElseThrow(() -> new BizException("啥情况啊,你怕不是一个黑客吧？？？"));

        int x = point.getIntValue("x");
        int y = point.getIntValue("y");

        Integer gameRole = gamePartaker.getGameRole();
        Preconditions.checkNotNull(gameRole);
        if (gameRole != 1 && gameRole != 2) {
            return;
        }
        if (!gameInfo.checkBoardPoint(gameRole, x, y)) {
            return;
        }
        boolean isWinner = gameInfo.isWinner(gameRole, x, y);
        if (isWinner) {
            gameInfo.setGameStatus(1);
            GameRecord entity = new GameRecord();
            entity.setId(gameId);
            entity.setWinnerId(gamePartaker.getUserId());
            entity.setGameEndTime(LocalDateTime.now());
            gameDomain.updateById(entity);
        }
        gameInfo.getGameWatchers()
                .forEach(it -> {
                    JSONObject checkPoint = new JSONObject();
                    checkPoint.put("color", gameRole);
                    checkPoint.put("x", x);
                    checkPoint.put("y", y);
                    sendMsg(it.getSession(), OutputMsg.of(MsgTypes.GAME_MSG_CHECK_BOARD, checkPoint).toTextMessage());
                    if (isWinner) {
                        sendMsg(gameInfo.getBlackUser().getSession(), OutputMsg.of(MsgTypes.GAME_MSG_GAME_OVER, gameRole).toTextMessage());
                        sendMsg(gameInfo.getWhiteUser().getSession(), OutputMsg.of(MsgTypes.GAME_MSG_GAME_OVER, gameRole).toTextMessage());
                        clearGame(gameId);
                    }
                });
    }


    private void touchGame(Integer gameId) {
        if (gameId == null) {
            return;
        }
        activeGames.put(gameId, LocalDateTime.now());
    }


    private void clearGame(Integer gameId) {
        if (gameId == null) {
            return;
        }
        GameInfo removedGame = onlineGames.remove(gameId);
        if (removedGame != null) {
            removedGame.getGameWatchers()
                    .forEach(it -> {
                        userGames.remove(it.getUserId());
                    });
        }
        activeGames.remove(gameId);
    }

    private OutputMsg<JSONObject> newGameInitMsg(GamePartaker currentUser, GamePartaker blackUser, GamePartaker whiteUser, boolean isGameWatcher) {
        JSONObject initMsg = new JSONObject();
        initMsg.put("isGameWatcher", isGameWatcher);

        JSONObject blackUserObj = null;
        if (blackUser != null) {
            blackUserObj = new JSONObject();
            blackUserObj.put("userName", blackUser.getUserName());
            blackUserObj.put("color", blackUser.getGameRole());
        }
        JSONObject whiteUserObj = null;
        if (whiteUser != null) {
            whiteUserObj = new JSONObject();
            whiteUserObj.put("userName", whiteUser.getUserName());
            whiteUserObj.put("color", whiteUser.getGameRole());
        }

        if (currentUser == blackUser) {
            initMsg.put("thisUser", blackUserObj);
            initMsg.put("thatUser", whiteUserObj);
        } else {
            initMsg.put("thisUser", whiteUserObj);
            initMsg.put("thatUser", blackUserObj);
        }
        return OutputMsg.of(MsgTypes.GAME_MSG_INIT_GAME, initMsg);
    }
}
