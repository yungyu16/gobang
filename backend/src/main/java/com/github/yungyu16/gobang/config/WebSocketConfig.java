/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import com.github.yungyu16.gobang.web.websocket.GameMsgHandler;
import com.github.yungyu16.gobang.web.websocket.UserMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private UserMsgHandler userMsgHandler;

    @Autowired
    private GameMsgHandler gameMsgHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(userMsgHandler, "ws/user").setAllowedOrigins("*");
        registry.addHandler(gameMsgHandler, "ws/game").setAllowedOrigins("*");
    }

    @Bean
    public UserMsgHandler userMsgHandler() {
        return new UserMsgHandler();
    }

    @Bean
    public GameMsgHandler gameMsgHandler() {
        return new GameMsgHandler();
    }
}