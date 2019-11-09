/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameMsgHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("hello world"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject json = JSON.parseObject(payload);
        System.out.println("=====接受到的数据" + json);
        session.sendMessage(new TextMessage("服务器返回收到的信息," + payload));
    }
}