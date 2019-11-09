/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import com.github.yungyu16.gobang.web.websocket.ChatMsgHandler;
import com.github.yungyu16.gobang.web.websocket.GameMsgHandler;
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
        registry.addHandler(chatMsgHandler(), "ws/chat").setAllowedOrigins("*");
        registry.addHandler(gameMsgHandler(), "ws/game").setAllowedOrigins("*");
    }

    public WebSocketHandler chatMsgHandler() {
        return new ChatMsgHandler();
    }

    public WebSocketHandler gameMsgHandler() {
        return new GameMsgHandler();
    }
}