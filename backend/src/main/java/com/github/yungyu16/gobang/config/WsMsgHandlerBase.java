package com.github.yungyu16.gobang.config;

import com.github.yungyu16.gobang.core.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.core.ws.msg.OutputMsg;
import com.github.yungyu16.gobang.exeception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.UUID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Slf4j
public class WsMsgHandlerBase extends AbstractWebSocketHandler {

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            MDC.put("traceId", UUID.randomUUID().toString());
            super.handleMessage(session, message);
        } catch (BizException e) {
            log.info("消息处理异常 {}", e.getMessage());
            session.sendMessage(OutputMsg.of(MsgTypes.USER_MSG_TOAST, e.getMessage()).toTextMessage());
        } catch (Exception e) {
            log.info("消息处理异常", e);
            session.sendMessage(OutputMsg.of(MsgTypes.USER_MSG_ERROR, e.getMessage()).toTextMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
