/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.config;

import com.github.yungyu16.gobang.annotation.WsMsgHandler;
import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.base.WsHandlerBase;
import com.github.yungyu16.gobang.constant.WsHandlerName;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends LogOperationsBase implements WebSocketConfigurer, InitializingBean, SmartInitializingSingleton {

    @Autowired
    private List<WsHandlerBase> wsHandlerBases;
    private Map<WsHandlerName, WsHandlerBase> wsHandlerMapping;

    @Override

    public void afterPropertiesSet() {
        if (!CollectionUtils.isEmpty(wsHandlerBases)) {
            wsHandlerBases.forEach(it -> {
                WsHandlerName wsHandlerName = it.handlerName();
                if (wsHandlerName == null) {
                    throw new NullPointerException("wsHandlerName");
                }
                wsHandlerMapping.put(wsHandlerName, it);
            });
        }
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        if (!CollectionUtils.isEmpty(wsHandlerBases)) {
            wsHandlerBases.forEach(it -> {
                WsHandlerName wsHandlerName = it.handlerName();
                if (wsHandlerName == null) {
                    throw new NullPointerException("wsHandlerName");
                }
                registry.addHandler(it, "ws/" + wsHandlerName.getPath()).setAllowedOrigins("*");
            });
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        if (ArrayUtils.isEmpty(beanDefinitionNames)) {
            return;
        }
        Arrays.stream(beanDefinitionNames).forEach(beanName -> {
            Object bean = applicationContext.getBean(beanName);
            Class<?> targetClass = AopUtils.getTargetClass(bean);

            ReflectionUtils.doWithMethods(targetClass, mt -> {
                WsMsgHandler annotation = mt.getAnnotation(WsMsgHandler.class);
                WsHandlerName wsHandlerName = annotation.handlerName();
                String msgType = annotation.msgType();
                String msgSubType = annotation.msgSubType();
                WsHandlerBase wsHandlerBase = wsHandlerMapping.get(wsHandlerName);
                if (wsHandlerBase != null) {
                    wsHandlerBase.addMsgHandler(msgType, msgSubType, (session, msg) -> mt.invoke(bean, session, msg));
                }
            }, mt -> mt.isAnnotationPresent(WsMsgHandler.class));
        });
    }
}