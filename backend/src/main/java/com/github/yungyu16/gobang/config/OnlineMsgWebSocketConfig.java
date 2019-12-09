/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import com.github.yungyu16.gobang.annotation.WsMsgHandler;
import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.constant.WsHandlerName;
import com.github.yungyu16.gobang.core.MsgHandler;
import com.github.yungyu16.gobang.web.ws.OnlineMsgHandler;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocket
public class OnlineMsgWebSocketConfig extends LogOperationsBase implements WebSocketConfigurer {

    @Autowired
    private OnlineMsgHandler onlineMsgHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(onlineMsgHandler, "ws/" + "online-msg").setAllowedOrigins("*");
    }
}