/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import com.github.yungyu16.gobang.web.websocket.GameMsgHandler;
import com.github.yungyu16.gobang.web.websocket.UserMsgHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(userMsgHandler(), "ws/chat").setAllowedOrigins("*");
        registry.addHandler(gameMsgHandler(), "ws/user").setAllowedOrigins("*");
    }

    public WebSocketHandler userMsgHandler() {
        return new UserMsgHandler();
    }

    public WebSocketHandler gameMsgHandler() {
        return new GameMsgHandler();
    }
}