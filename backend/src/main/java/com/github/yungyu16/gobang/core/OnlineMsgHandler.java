package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.config.WsMsgHandlerBase;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
public class OnlineMsgHandler extends WsMsgHandlerBase {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }
}
