package com.github.yungyu16.gobang.core;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
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
import java.util.Objects;
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
                .scheduleAtFixedRate(() -> {
                    log.info("开始刷新连接列表...");
                    LocalDateTime now = LocalDateTime.now();
                    latestTimeMappings.forEach((session, value) -> {
                        long seconds = Duration.between(now, value)
                                .abs()
                                .getSeconds();
                        if (seconds >= 20) {
                            sendMsg(session, WsOutputMsg.of("error", "连接超时..."));
                            doInEventLoop(session, () -> {
                                sessionMappings.remove(session);
                                latestTimeMappings.remove(session);
                            });
                            close(session);
                        }
                    });
                }, 0, 10, TimeUnit.SECONDS);
        eventLoops.get(1)
                .scheduleAtFixedRate(() -> {
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
                    log.info("开始推送在线用户列表...");
                    sessionMappings.forEach((key, value) -> {
                        List<JSONObject> thisUserList = userList.stream()
                                .filter(it -> !Objects.equals(it.getInteger("userId"), value.getId()))
                                .collect(Collectors.toList());
                        sendMsg(key, WsOutputMsg.of(BaseWsHandler.TYPE_USER_LIST, thisUserList));
                    });


                }, 0, 20, TimeUnit.SECONDS);
    }

    public void touch(WebSocketSession webSocketSession) {
        if (webSocketSession == null) {
            return;
        }
        latestTimeMappings.computeIfPresent(webSocketSession, (k, ov) -> LocalDateTime.now());
    }

    public void auth(String sessionToken, WebSocketSession webSocketSession) {
        getSessionAttr(sessionToken, USER_ID)
                .map(it -> {
                    UserRecord userDomainById = userDomain.getById(JSON.parseObject(it, Integer.class));
                    if (userDomainById == null) {
                        throw new BizException("用户不存在");
                    }
                    sessionMappings.put(webSocketSession, userDomainById);
                    latestTimeMappings.put(webSocketSession, LocalDateTime.now());
                    return it;
                }).orElseThrow(() -> new BizException("用户不存在"));
    }

    public boolean hasAuth(WebSocketSession webSocketSession) {

        if (webSocketSession == null) {
            return false;
        }
        return sessionMappings.get(webSocketSession) != null;
    }

    public void sendMsg(WebSocketSession webSocketSession, WsOutputMsg msg) {
        ConditionTools.checkNotNull(webSocketSession);
        ConditionTools.checkNotNull(msg);
        doInEventLoop(webSocketSession, () -> {
            try {
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
