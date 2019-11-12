package com.github.yungyu16.gobang.core;

import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.core.game.gobang.GobangContext;
import com.github.yungyu16.gobang.core.game.gobang.entity.UserInfo;
import com.github.yungyu16.gobang.core.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.core.ws.msg.OutputMsg;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.event.SessionTokenEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Component
public class OnlineUserContext extends LogOperationsBase implements InitializingBean, ApplicationListener<SessionTokenEvent> {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("refresh-user-th-%s").build());

    private Map<Integer, WebSocketSession> onlineUsers = Maps.newConcurrentMap();

    private Map<Integer, LocalDateTime> activeUsers = Maps.newConcurrentMap();

    private Cache<Integer, UserRecord> userRecordCache = CacheBuilder.newBuilder()
            .build();

    @Autowired
    private UserDomain userDomain;

    @Autowired
    private GobangContext gobangContext;

    private Map<String, UserInfo> sessionUserMappings = Maps.newConcurrentMap();

    private Map<WebSocketSession, String> sessionTokenMappings = Maps.newConcurrentMap();

    /**
     * 是否在线
     *
     * @param userId
     *
     * @return
     */
    public boolean isOnline(@NotNull Integer userId) {
        return true;
    }

    public Optional<UserRecord> getOnlineUser(@NotNull Integer userId) {
        return null;
    }

    public List<UserRecord> listOnlineUser() {
        return null;
    }

    /**
     * 新增在线用户
     *
     * @param userId
     * @param wsSession
     */
    public void newOnlineUser(@NotNull Integer userId, @NotNull WebSocketSession wsSession) {

    }

    /**
     * 心跳
     *
     * @param wsSession
     */
    public void touchUser(@NotNull WebSocketSession wsSession) {

    }

    /**
     * 淘汰过期用户
     *
     * @param userId
     */
    public void knickOutUser(@NotNull Integer userId) {

    }

    /**
     * 发送消息
     *
     * @param userId
     * @param msg
     */
    public void sendMsg(@NotNull Integer userId, @NotNull WebSocketMessage<?> msg) {

    }

    /**
     * 群发消息
     *
     * @param userIds
     * @param msg
     */
    public void groupSendMsg(@NotNull Set<Integer> userIds, @NotNull WebSocketMessage<?> msg) {

    }

    public void sendMsg2User(Integer userId, OutputMsg msg) {
        if (userId == null) {
            return;
        }
        if (msg == null) {
            return;
        }
        getUserInfoByUserId(userId)
                .map(it -> {
                    UserRecord userRecord = it.getUserRecord();
                    WebSocketSession socketSession = it.getSocketSession();
                    log.info("开始发送消息给 {}", userRecord.getUserName());
                    sendMsg(socketSession, msg.toTextMessage());
                    return 1;
                })
                .orElseGet(() -> {
                    log.info("用户ws会话不存在...{}", userId);
                    return 1;
                });
    }

    public Optional<WebSocketSession> getWsSessionByUserId(Integer userId) {
        return getUserInfoByUserId(userId)
                .map(UserInfo::getSocketSession);
    }

    public Optional<UserInfo> getUserInfoByUserId(Integer userId) {
        Optional<String> sessionTokenOpt = getSessionTokenByUserId(userId);
        if (!sessionTokenOpt.isPresent()) {
            return Optional.empty();
        }
        String sessionToken = sessionTokenOpt.get();
        return getUserInfoBySessionToken(sessionToken);
    }

    public Optional<String> getSessionTokenByUserId(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userIdTokenMappings.get(userId));
    }

    public Optional<UserInfo> getUserInfoBySessionToken(String token) {
        if (StringUtils.isBlank(token)) {
            return Optional.empty();
        }
        UserInfo userInfo = sessionUserMappings.get(token);
        return Optional.of(userInfo);
    }

    public Optional<String> getSessionTokenByWsSession(WebSocketSession session) {
        if (session == null) {
            return Optional.empty();
        }
        String token = sessionTokenMappings.get(session);
        return Optional.ofNullable(token);
    }

    @Override
    public void afterPropertiesSet() {
        executorService.scheduleAtFixedRate(this::refreshActiveUser, 0, 10, TimeUnit.SECONDS);
    }

    public void newUserInfo(WebSocketSession session, String sessionToken) {
        getSessionUserId(sessionToken)
                .map(userId -> {
                    UserRecord userDomainById = userDomain.getById(userId);
                    if (userDomainById == null) {
                        log.info("用户不存在...{} {}", userId, sessionToken);
                        return 1;
                    }
                    //log.info("收到 {} 用户的心跳...", userDomainById.getUserName());
                    UserInfo newUserInfo = new UserInfo(session, userDomainById);
                    sessionUserMappings.put(sessionToken, newUserInfo);
                    activeTokenMappings.put(sessionToken, LocalDateTime.now());
                    userIdTokenMappings.put(userId, sessionToken);
                    sessionTokenMappings.put(session, sessionToken);
                    return 1;
                })
                .orElseGet(() -> {
                    log.info("session已失效...");
                    return 1;
                });
    }

    public void discardUserInfo(String sessionToken) {
        UserInfo userInfo = sessionUserMappings.get(sessionToken);
        if (userInfo == null) {
            sessionUserMappings.remove(sessionToken);
            activeTokenMappings.remove(sessionToken);
            return;
        }
        WebSocketSession session = userInfo.getSocketSession();
        sendMsg(session, OutputMsg.of("error", "会话过期...").toTextMessage());
        doInEventLoop(session, () -> {
            UserRecord userRecord = userInfo.getUserRecord();
            log.info("会话过期,移除当前会话....{}", userRecord.getUserName());
            Integer id = userRecord.getId();
            userIdTokenMappings.remove(id);
            sessionUserMappings.remove(sessionToken);
            activeTokenMappings.remove(sessionToken);
            sessionTokenMappings.remove(session);
        });
        close(session);
    }

    @Override
    public void onApplicationEvent(SessionTokenEvent event) {
        log.info("用户退出,删除失效session...");
        String type = event.getType();
        if (StringUtils.equalsIgnoreCase(SessionTokenEvent.TYPE_REMOVE, type)) {
            String token = event.getToken();
            if (StringUtils.isBlank(token)) {
                return;
            }
            discardUserInfo(token);
        }
    }

    private void refreshActiveUser() {
        //log.info("开始刷新连接列表...");
        LocalDateTime now = LocalDateTime.now();
        activeTokenMappings.forEach((token, value) -> {
            long seconds = Duration.between(now, value).abs().getSeconds();
            if (seconds >= 10) {
                discardUserInfo(token);
            }
        });
    }

    public List<Map<String, Object>> getOnlineUsers(String sessionToken) {
        Integer currentUserId = null;
        if (StringUtils.isNotBlank(sessionToken)) {
            UserInfo userInfo = sessionUserMappings.get(sessionToken);
            if (userInfo != null) {
                currentUserId = userInfo.getUserRecord().getId();
            }
        }

        Map<Integer, Map<String, Object>> finalUserMap = Maps.newHashMap();

        Integer finalCurrentUserId = currentUserId;
        userDomain.list()
                .stream()
                .filter(it -> finalCurrentUserId == null || !Objects.equals(it.getId(), finalCurrentUserId))
                .forEach(it -> {
                    Map<String, Object> userInfo = new JSONObject();
                    String userName = it.getUserName();
                    Integer userId = it.getId();
                    userInfo.put("userId", userId);
                    userInfo.put("userName", userName);
                    userInfo.put("status", 0);
                    finalUserMap.put(it.getId(), userInfo);
                });

        sessionUserMappings.values()
                .stream()
                .filter(it -> finalCurrentUserId == null || !Objects.equals(it.getUserRecord().getId(), finalCurrentUserId))
                .forEach(it -> {
                    Map<String, Object> userInfo = new JSONObject();
                    UserRecord userRecord = it.getUserRecord();
                    String userName = userRecord.getUserName();
                    Integer userId = userRecord.getId();
                    userInfo.put("userId", userId);
                    userInfo.put("userName", userName);
                    userInfo.put("status", -3);
                    gobangContext.userGame(userId)
                            .ifPresent(partaker -> {
                                userInfo.put("gameId", partaker.getGameId());
                                Integer gameRole = partaker.getGameRole();
                                if (gameRole == 3) {
                                    userInfo.put("status", -1);
                                } else {
                                    userInfo.put("status", -2);
                                }
                            });
                    finalUserMap.put(userId, userInfo);
                });
        return finalUserMap
                .values()
                .stream()
                .sorted(Comparator.comparing(it -> (Integer) it.get("status")))
                .collect(Collectors.toList());
    }

    public void ping(WebSocketSession webSocketSession, String sessionToken) {
        if (StringUtils.isBlank(sessionToken)) {
            log.info("sessionToken为空");
            return;
        }
        touch(sessionToken);
        UserInfo userInfo = sessionUserMappings.get(sessionToken);
        if (userInfo != null) {
            //log.info("收到 {} 用户的心跳...", userInfo.getUserRecord().getUserName());
            return;
        }
        newUserInfo(webSocketSession, sessionToken);
    }

    public void pushOnlineUsers(WebSocketSession webSocketSession, String sessionToken) {
        List<Map<String, Object>> onlineUsers = getOnlineUsers(sessionToken);
        //log.info("当前用户列表：{}", JSON.toJSONString(onlineUsers));
        sendMsg(webSocketSession, OutputMsg.of(MsgTypes.USER_MSG_USER_LIST, onlineUsers).toTextMessage());
    }

    private void touch(String sessionToken) {
        if (StringUtils.isBlank(sessionToken)) {
            return;
        }
        activeTokenMappings.put(sessionToken, LocalDateTime.now());
    }
}
