package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.base.WsMsgHandlerBase;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public class OnlineMsgHandler extends WsMsgHandlerBase implements BeanPostProcessor {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }
}
