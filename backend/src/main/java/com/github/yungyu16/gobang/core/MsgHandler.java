package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.ws.msg.InputMsg;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;

/**
 * CreatedDate: 2019/11/15
 * Author: songjialin
 */
public interface MsgHandler {
    void whenInputMsg(WebSocketSession session, InputMsg inputMsg) throws InvocationTargetException, IllegalAccessException;
}
