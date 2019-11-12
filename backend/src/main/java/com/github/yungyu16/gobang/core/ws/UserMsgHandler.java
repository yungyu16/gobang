/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.core.ws;


import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.core.OnlineUserContext;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.core.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.core.ws.msg.OutputMsg;
import com.github.yungyu16.gobang.core.ws.msg.UserInputMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

public class UserMsgHandler extends TextWebSocketHandler {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private OnlineUserContext onlineUserContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(OutputMsg.of(MsgTypes.USER_MSG_WELCOME, "Welcome~").toTextMessage());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MDC.put("traceId", UUID.randomUUID().toString());
        String payload = message.getPayload();
        UserInputMsg wsInputMsg = JSON.parseObject(payload, UserInputMsg.class);
        try {
            String msgType = wsInputMsg.getMsgType();
            if (StringUtils.isBlank(msgType)) {
                throw new BizException("msgType为空");
            }
            switch (msgType) {
                case MsgTypes.USER_MSG_PING:
                    //log.info("开始处理心跳消息...");
                    String sessionToken = wsInputMsg.getSessionToken();
                    onlineUserContext.ping(session, sessionToken);
                    onlineUserContext.pushOnlineUsers(session, sessionToken);
                    //log.info("ping成功");
                    break;
                default:
                    log.info("不支持的消息类型：{}", msgType);
            }
        } catch (BizException e) {
            log.info("消息处理异常 {}", e.getMessage());
            session.sendMessage(OutputMsg.of(MsgTypes.USER_MSG_TOAST, e.getMessage()).toTextMessage());
        } catch (Exception e) {
            log.info("消息处理异常", e);
            session.sendMessage(OutputMsg.of(MsgTypes.USER_MSG_ERROR, e.getMessage()).toTextMessage());
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("ws链接关闭...删除session");
        onlineUserContext.getSessionTokenByWsSession(session)
                .ifPresent(it -> {
                    onlineUserContext.discardUserInfo(it);
                });
    }
}