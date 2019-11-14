package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.event.SignOutEvent;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Component
public class OnlineUserContext extends LogOperationsBase implements InitializingBean, ApplicationListener<SignOutEvent> {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("refresh-user-th-%s").build());
    private Map<Integer, WebSocketSession> onlineUsers = Maps.newConcurrentMap();
    private Map<Integer, LocalDateTime> activeUsers = Maps.newConcurrentMap();

    @Autowired
    private UserDomain userDomain;
    @Autowired
    private WsSocketOperations wsSocketOperations;

    @Override
    public void afterPropertiesSet() {
        executorService.scheduleAtFixedRate(this::refreshActiveUser, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onApplicationEvent(SignOutEvent event) {
        log.info("用户退出,删除失效session...");
        Integer userId = event.getUserId();
        if (userId != null) {
            kickOutUser(userId);
        }
    }

    /**
     * 是否在线
     *
     * @param userId
     * @return
     */
    public boolean isOnline(@NotNull Integer userId) {
        if (userId == null) {
            return false;
        }
        return onlineUsers.containsKey(userId);
    }

    public Optional<UserRecord> getOnlineUser(@NotNull Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }
        if (!onlineUsers.containsKey(userId)) {
            return Optional.empty();
        }
        return userDomain.getUserInfoThroughCache(userId);
    }

    public List<UserRecord> listOnlineUser() {
        return onlineUsers.keySet()
                .stream()
                .map(it -> getOnlineUser(it).orElse(null))
                .collect(Collectors.toList());
    }

    /**
     * 新增在线用户
     *
     * @param userId
     * @param wsSession
     */
    public synchronized void newOnlineUser(@NotNull Integer userId, @NotNull WebSocketSession wsSession) {
        if (userId == null) {
            return;
        }
        if (wsSession == null) {
            return;
        }
        wsSocketOperations.putSessionUserId(userId, wsSession);
        onlineUsers.put(userId, wsSession);
        activeUsers.put(userId, LocalDateTime.now());
    }

    /**
     * 心跳
     *
     * @param wsSession
     */
    public void touchUser(@NotNull WebSocketSession wsSession) {
        wsSocketOperations.getSessionUserId(wsSession)
                .ifPresent(it -> {
                    activeUsers.put(it, LocalDateTime.now());
                });
    }

    /**
     * 淘汰过期用户
     *
     * @param userId
     */
    public synchronized void kickOutUser(@NotNull Integer userId) {
        if (userId == null) {
            return;
        }
        activeUsers.remove(userId);
        onlineUsers.remove(userId);
    }

    /**
     * 群发消息
     *
     * @param userIds
     * @param msg
     */
    public void groupSendMsg(@NotNull Set<Integer> userIds, @NotNull WebSocketMessage<?> msg) {
        if (userIds == null) {
            return;
        }
        if (userIds.isEmpty()) {
            return;
        }
        if (msg == null) {
            return;
        }
        userIds.forEach(userId -> {
            sendMsg(userId, msg);
        });
    }

    /**
     * 发送消息
     *
     * @param userId
     * @param msg
     */
    public void sendMsg(@NotNull Integer userId, @NotNull WebSocketMessage<?> msg) {
        if (userId == null) {
            return;
        }
        if (msg == null) {
            return;
        }
        WebSocketSession webSocketSession = onlineUsers.get(userId);
        if (webSocketSession == null) {
            log.info("用户ws会话不存在...{}", userId);
            return;
        }
        wsSocketOperations.sendMsg(webSocketSession, msg);
    }

    private void refreshActiveUser() {
        LocalDateTime now = LocalDateTime.now();
        activeUsers.forEach((userId, value) -> {
            long seconds = Duration.between(now, value).abs().getSeconds();
            if (seconds >= 10) {
                kickOutUser(userId);
            }
        });
    }

    //public List<Map<String, Object>> getOnlineUsers(String sessionToken) {
    //    Integer currentUserId = null;
    //    if (StringUtils.isNotBlank(sessionToken)) {
    //        UserInfo userInfo = sessionUserMappings.get(sessionToken);
    //        if (userInfo != null) {
    //            currentUserId = userInfo.getUserRecord().getId();
    //        }
    //    }
    //
    //    Map<Integer, Map<String, Object>> finalUserMap = Maps.newHashMap();
    //
    //    Integer finalCurrentUserId = currentUserId;
    //    userDomain.list()
    //            .stream()
    //            .filter(it -> finalCurrentUserId == null || !Objects.equals(it.getId(), finalCurrentUserId))
    //            .forEach(it -> {
    //                Map<String, Object> userInfo = new JSONObject();
    //                String userName = it.getUserName();
    //                Integer userId = it.getId();
    //                userInfo.put("userId", userId);
    //                userInfo.put("userName", userName);
    //                userInfo.put("status", 0);
    //                finalUserMap.put(it.getId(), userInfo);
    //            });
    //
    //    sessionUserMappings.values()
    //            .stream()
    //            .filter(it -> finalCurrentUserId == null || !Objects.equals(it.getUserRecord().getId(), finalCurrentUserId))
    //            .forEach(it -> {
    //                Map<String, Object> userInfo = new JSONObject();
    //                UserRecord userRecord = it.getUserRecord();
    //                String userName = userRecord.getUserName();
    //                Integer userId = userRecord.getId();
    //                userInfo.put("userId", userId);
    //                userInfo.put("userName", userName);
    //                userInfo.put("status", -3);
    //                gobangContext.userGame(userId)
    //                        .ifPresent(partaker -> {
    //                            userInfo.put("gameId", partaker.getGameId());
    //                            Integer gameRole = partaker.getGameRole();
    //                            if (gameRole == 3) {
    //                                userInfo.put("status", -1);
    //                            } else {
    //                                userInfo.put("status", -2);
    //                            }
    //                        });
    //                finalUserMap.put(userId, userInfo);
    //            });
    //    return finalUserMap
    //            .values()
    //            .stream()
    //            .sorted(Comparator.comparing(it -> (Integer) it.get("status")))
    //            .collect(Collectors.toList());
    //}
}
