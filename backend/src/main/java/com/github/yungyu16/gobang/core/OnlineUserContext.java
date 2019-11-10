package com.github.yungyu16.gobang.core;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import cn.xiaoshidai.common.toolkit.base.StringTools;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.base.SessionOperationBase;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.web.websocket.BaseWsHandler;
import com.github.yungyu16.gobang.web.websocket.entity.WsOutputMsg;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Component
public class OnlineUserContext extends SessionOperationBase implements InitializingBean {

    @Autowired
    private UserDomain userDomain;

    public String EVENT_LOOP_KEY = "EVENT_LOOP";

    private AtomicInteger eventLoopIndex = new AtomicInteger();

    private Map<WebSocketSession, UserRecord> sessionMappings = Maps.newConcurrentMap();

    private Map<WebSocketSession, LocalDateTime> latestTimeMappings = Maps.newConcurrentMap();

    private List<ScheduledExecutorService> eventLoops = IntStream.range(0, 10)
            .mapToObj(it -> Executors.newScheduledThreadPool(1))
            .collect(Collectors.toList());

    @Override
    public void afterPropertiesSet() throws Exception {
        eventLoops.get(0)
                .scheduleAtFixedRate(this::refreshActiveUser, 0, 10, TimeUnit.SECONDS);
    }

    private void refreshActiveUser() {
        log.info("开始刷新连接列表...{}", latestTimeMappings.size());
        LocalDateTime now = LocalDateTime.now();
        latestTimeMappings.forEach((session, value) -> {
            long seconds = Duration.between(now, value)
                    .abs()
                    .getSeconds();
            if (seconds >= 20) {
                sendMsg(session, WsOutputMsg.of("error", "会话过期..."));
                doInEventLoop(session, () -> {
                    log.info("会话过期,移除当前会话....");
                    sessionMappings.remove(session);
                    latestTimeMappings.remove(session);
                });
                close(session);
            }
        });
    }

    private void pushOnlineUserList(WebSocketSession webSocketSession) {
        List<JSONObject> userList = sessionMappings.values()
                .stream()
                .map(it -> {
                    JSONObject userInfo = new JSONObject();
                    String userName = it.getUserName();
                    String status = "空闲";
                    userInfo.put("userId", it.getId());
                    userInfo.put("userName", userName);
                    userInfo.put("status", status);
                    return userInfo;
                }).collect(Collectors.toList());
        log.info("开始推送在线用户列表...{}", userList.size());
        List<JSONObject> thisUserList = userList.stream()
                //.filter(it -> !Objects.equals(it.getInteger("userId"), value.getId()))
                .collect(Collectors.toList());
        sendMsg(webSocketSession, WsOutputMsg.of(BaseWsHandler.TYPE_USER_LIST, thisUserList));
    }

    public void touch(WebSocketSession webSocketSession) {
        if (webSocketSession == null) {
            return;
        }
        latestTimeMappings.put(webSocketSession, LocalDateTime.now());
        log.info("更新最近访问时间成功");
    }

    public void ping(String sessionToken, WebSocketSession webSocketSession) {
        touch(webSocketSession);
        pushOnlineUserList(webSocketSession);
        if (StringTools.isBlank(sessionToken)) {
            return;
        }
        UserRecord userRecord = sessionMappings.get(webSocketSession);
        if (userRecord != null) {
            log.info("收到 {} 用户的心跳...", userRecord.getUserName());
            return;
        }
        getSessionAttr(sessionToken, USER_ID)
                .map(it -> {
                    Integer userId = JSON.parseObject(it, Integer.class);
                    UserRecord userDomainById = userDomain.getById(userId);
                    if (userDomainById == null) {
                        log.info("用户不存在...{} {}", userId, sessionToken);
                        return it;
                    }
                    sessionMappings.put(webSocketSession, userDomainById);
                    latestTimeMappings.put(webSocketSession, LocalDateTime.now());
                    pushOnlineUserList(webSocketSession);
                    return it;
                });
    }

    public void sendMsg(WebSocketSession webSocketSession, WsOutputMsg msg) {
        ConditionTools.checkNotNull(webSocketSession);
        ConditionTools.checkNotNull(msg);
        doInEventLoop(webSocketSession, () -> {
            try {
                log.info("开始发送消息：{}", JSON.toJSONString(msg));
                webSocketSession.sendMessage(msg.toTextMessage());
            } catch (IOException e) {
                log.error("发送消息异常", e);
            }
        });
    }

    public void close(WebSocketSession webSocketSession) {
        ConditionTools.checkNotNull(webSocketSession);
        doInEventLoop(webSocketSession, () -> {
            try {
                webSocketSession.close();
            } catch (IOException e) {
                log.error("关闭连接异常", e);
            }
        });
    }

    public void doInEventLoop(WebSocketSession webSocketSession, Runnable runnable) {
        ConditionTools.checkNotNull(webSocketSession);
        ConditionTools.checkNotNull(runnable);
        ScheduledExecutorService eventLoop = getEventLoop(webSocketSession);
        eventLoop.execute(runnable);
    }


    private ScheduledExecutorService getEventLoop(WebSocketSession webSocketSession) {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        Object eventLoop = attributes.get(EVENT_LOOP_KEY);
        if (eventLoop == null) {
            int idx = nextEventLoopIdx();
            ScheduledExecutorService scheduledExecutorService = eventLoops.get(idx);
            attributes.put(EVENT_LOOP_KEY, scheduledExecutorService);
            eventLoop = scheduledExecutorService;
        }
        return (ScheduledExecutorService) eventLoop;
    }

    private synchronized int nextEventLoopIdx() {
        if (eventLoopIndex.get() >= eventLoops.size()) {
            eventLoopIndex.set(0);
        }
        return eventLoopIndex.getAndIncrement();
    }
}
