/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.websocket;

import com.github.yungyu16.gobang.core.OnlineUserContext;
import com.github.yungyu16.gobang.web.websocket.entity.WsInputMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

public class UserMsgHandler extends BaseWsHandler {

    @Autowired
    private OnlineUserContext userContext;

    @Override
    protected void handlerMsg(WebSocketSession session, WsInputMsg inputMsg) {
        String msgType = inputMsg.getMsgType();
        userContext.touch(session);
        switch (msgType) {
            case TYPE_PING:
                log.info("开始处理心跳消息...");
                String sessionToken = inputMsg.getData();
                userContext.ping(sessionToken, session);
                log.info("ping成功");
                break;
            default:
                log.info("不支持的消息类型：{}", msgType);
        }
    }
}