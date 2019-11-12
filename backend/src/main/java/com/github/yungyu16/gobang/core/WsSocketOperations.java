package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.yungyu16.gobang.constant.SessionConstants.USER_ID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public class WsSocketOperations extends LogOperationsBase {

    private AtomicInteger eventLoopIndex = new AtomicInteger();

    private List<ScheduledExecutorService> eventLoops = IntStream.range(0, 10)
            .mapToObj(it -> Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("message-" + it + "-th-%s").build()))
            .collect(Collectors.toList());

    public void sendMsg(WebSocketSession webSocketSession, TextMessage message) {
        Preconditions.checkNotNull(webSocketSession);
        Preconditions.checkNotNull(message);
        doInEventLoop(webSocketSession, () -> {
            try {
                Object userId = webSocketSession.getAttributes().get(USER_ID);
                log.info("开始发送ws消息：userId:{} msg:{}", userId, message.getPayload());
                webSocketSession.sendMessage(message);
            } catch (IOException e) {
                log.error("发送消息异常", e);
            }
        });
    }

    public void close(WebSocketSession webSocketSession) {
        Preconditions.checkNotNull(webSocketSession);
        doInEventLoop(webSocketSession, () -> {
            try {
                webSocketSession.close();
            } catch (IOException e) {
                log.error("关闭连接异常", e);
            }
        });
    }

    public void doInEventLoop(WebSocketSession webSocketSession, Runnable runnable) {
        Preconditions.checkNotNull(webSocketSession);
        Preconditions.checkNotNull(runnable);
        ScheduledExecutorService eventLoop = getEventLoop(webSocketSession);
        eventLoop.execute(runnable);
    }

    private synchronized ScheduledExecutorService getEventLoop(WebSocketSession webSocketSession) {
        Map<String, Object> attributes = webSocketSession.getAttributes();
        String EVENT_LOOP_KEY = "EVENT_LOOP";
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
