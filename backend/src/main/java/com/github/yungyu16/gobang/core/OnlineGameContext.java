package com.github.yungyu16.gobang.core;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.base.WebSockOperationBase;
import com.github.yungyu16.gobang.core.entity.GameInfo;
import com.github.yungyu16.gobang.core.entity.GamePartaker;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.GameDomain;
import com.github.yungyu16.gobang.web.websocket.msg.MsgTypes;
import com.github.yungyu16.gobang.web.websocket.msg.OutputMsg;
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
public class OnlineGameContext extends WebSockOperationBase implements InitializingBean {

    @Autowired
    private GameDomain gameDomain;

    private Map<Integer, GameInfo> onlineGames = Maps.newConcurrentMap();

    private Map<Integer, LocalDateTime> activeGames = Maps.newConcurrentMap();

    private Map<Integer, GamePartaker> userGames = Maps.newConcurrentMap();

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("refresh-game-th-%s").build());

    @Override
    public void afterPropertiesSet() {
        executorService.scheduleAtFixedRate(this::refreshActiveGame, 0, 1, TimeUnit.MINUTES);
    }

    private void refreshActiveGame() {
        log.info("开始刷新对局列表...");
        LocalDateTime now = LocalDateTime.now();
        activeGames.forEach((gameId, value) -> {
            long minutes = Duration.between(now, value).abs().toMinutes();
            if (minutes >= 10) {
                onlineGames.remove(gameId);
                activeGames.remove(gameId);
            }
        });
    }

    public void newGame(Integer gameId) {
        ConditionTools.checkNotNull(gameId);
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
            throw new BizException("对局不存在...");
        }
        if (gameInfo.isGameOver()) {
            throw new BizException("对局已结束...");
        }
        UserRecord userRecord = getCurrentUserRecord(sessionToken).orElseThrow(() -> new BizException("用户未登录"));
        if (gameId == null) {
            throw new BizException("对局不存在...");
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
                gameInfo.setGameStatus(1);
                TextMessage msg = OutputMsg.of(MsgTypes.GAME_MSG_START_GAME, "").toTextMessage();
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
            throw new BizException("对局不存在...");
        }
        Integer userId = userRecord.getId();
        GameInfo gameInfo = onlineGames.get(gameId);
        if (gameInfo == null) {
            throw new BizException("对局不存在...");
        }

        GamePartaker gamePartaker = gameInfo.getGamePartaker(userId).orElseThrow(() -> new BizException("啥情况啊,你怕不是一个黑客吧？？？"));

        int x = point.getIntValue("x");
        int y = point.getIntValue("y");

        Integer gameRole = gamePartaker.getGameRole();
        ConditionTools.checkNotNull(gameRole);
        if (gameRole != 1 && gameRole != 2) {
            return;
        }
        if (!gameInfo.checkBoardPoint(gameRole, x, y)) {
            return;
        }
        boolean isWinner = gameInfo.isWinner(gameRole, x, y);
        if (isWinner) {
            gameInfo.setGameStatus(1);
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
                    }
                });
    }


    public void touchGame(Integer gameId) {
        if (gameId == null) {
            return;
        }
        activeGames.put(gameId, LocalDateTime.now());
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
