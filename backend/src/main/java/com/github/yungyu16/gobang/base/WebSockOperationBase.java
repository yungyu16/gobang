package com.github.yungyu16.gobang.base;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import cn.xiaoshidai.common.toolkit.base.StringTools;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.web.websocket.msg.MsgTypes;
import com.github.yungyu16.gobang.web.websocket.msg.OutputMsg;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
public abstract class WebSockOperationBase extends SessionOperationBase {
    private static AtomicInteger eventLoopIndex = new AtomicInteger();
    private static List<ScheduledExecutorService> eventLoops = IntStream.range(0, 10)
            .mapToObj(it -> Executors.newScheduledThreadPool(1))
            .collect(Collectors.toList());

    public void sendMsg(WebSocketSession webSocketSession, OutputMsg msg) {
        ConditionTools.checkNotNull(webSocketSession);
        ConditionTools.checkNotNull(msg);
        doInEventLoop(webSocketSession, () -> {
            try {
                Object sessionToken = webSocketSession.getAttributes().get("sessionToken");
                if (!StringTools.equalsIgnoreCase(msg.getMsgType(), MsgTypes.USER_MSG_USER_LIST)) {
                    log.info("开始发送ws消息：{} {}", JSON.toJSONString(msg), sessionToken);
                }
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

    protected ScheduledExecutorService getEventLoop(WebSocketSession webSocketSession) {
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

    protected synchronized int nextEventLoopIdx() {
        if (eventLoopIndex.get() >= eventLoops.size()) {
            eventLoopIndex.set(0);
        }
        return eventLoopIndex.getAndIncrement();
    }

}
