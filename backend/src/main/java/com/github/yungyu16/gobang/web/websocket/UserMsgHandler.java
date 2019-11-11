/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.websocket;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.core.OnlineUserContext;
import com.github.yungyu16.gobang.web.websocket.msg.MsgTypes;
import com.github.yungyu16.gobang.web.websocket.msg.OutputMsg;
import com.github.yungyu16.gobang.web.websocket.msg.UserInputMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        MDC.put("traceId", StringTools.UUID());
        String payload = message.getPayload();
        UserInputMsg wsInputMsg = JSON.parseObject(payload, UserInputMsg.class);
        try {
            String msgType = wsInputMsg.getMsgType();
            if (StringTools.isBlank(msgType)) {
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