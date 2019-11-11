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
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
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
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("refresh-game-thread-%s").build());

    @Override
    public void afterPropertiesSet() throws Exception {
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

    public synchronized void enterGame(WebSocketSession session, String sessionToken, Integer gameId) {
        GameInfo gameInfo = onlineGames.get(gameId);
        if (gameInfo == null) {
            throw new BizException("对局不存在...");
        }
        UserRecord userRecord = getCurrentUserRecord(sessionToken).orElseThrow(() -> new BizException("用户未登录"));
        if (gameId == null) {
            throw new BizException("对局不存在...");
        }

        GamePartaker gamePartaker = gameInfo.enterGame(userRecord);
        log.info("当前用户角色为：{}", gamePartaker.getGameRole());
        gamePartaker.setSession(session);

        boolean isGameWatcher = gamePartaker.isGameWatcher();

        GamePartaker blackUser = gameInfo.getBlackUser();
        GamePartaker whiteUser = gameInfo.getWhiteUser();

        if (isGameWatcher) {
            OutputMsg<JSONObject> initMsg = newGameInitMsg(gamePartaker, blackUser, whiteUser, true);
            sendMsg(gamePartaker.getSession(), initMsg);
        } else {
            if (blackUser != null) {
                OutputMsg<JSONObject> initMsg = newGameInitMsg(gamePartaker, blackUser, whiteUser, false);
                sendMsg(blackUser.getSession(), initMsg);
            }
            if (whiteUser != null) {
                OutputMsg<JSONObject> initMsg = newGameInitMsg(gamePartaker, blackUser, whiteUser, false);
                sendMsg(whiteUser.getSession(), initMsg);
            }
            if (blackUser != null && whiteUser != null) {
                sendMsg(blackUser.getSession(), OutputMsg.of(MsgTypes.GAME_MSG_START_GAME, ""));
                sendMsg(whiteUser.getSession(), OutputMsg.of(MsgTypes.GAME_MSG_START_GAME, ""));
            }
        }
    }

    public void checkBoardPoint(String sessionToken, Integer gameId, JSONObject point) {
        UserRecord userRecord = getCurrentUserRecord(sessionToken).orElseThrow(() -> new BizException("用户未登录"));
        if (gameId == null) {
            throw new BizException("对局不存在...");
        }
        Integer userId = userRecord.getId();
        GameInfo gameInfo = onlineGames.get(gameId);
        if (gameInfo == null) {
            throw new BizException("对局不存在...");
        }
        String x = point.getString("x");
        String y = point.getString("y");

        int userColor = gameInfo.userColor(userId);
        if (userColor <= 0) {
            throw new BizException("非法操作");
        }
        gameInfo.getGameWatchers()
                .forEach(it -> {
                    JSONObject checkPoint = new JSONObject();
                    checkPoint.put("color", userColor);
                    checkPoint.put("x", x);
                    checkPoint.put("y", y);
                    sendMsg(it.getSession(), OutputMsg.of(MsgTypes.GAME_MSG_CHECK_BOARD, checkPoint));
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

        if (currentUser == blackUser) {
            initMsg.put("thisUser", blackUser);
            initMsg.put("thatUser", whiteUser);
        } else {
            initMsg.put("thisUser", whiteUser);
            initMsg.put("thatUser", blackUser);
        }
        return OutputMsg.of(MsgTypes.GAME_MSG_INIT_GAME, initMsg);
    }
}
