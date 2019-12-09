package com.github.yungyu16.gobang.core.chat;

import com.github.yungyu16.gobang.annotation.WsMsgHandler;
import com.github.yungyu16.gobang.core.MsgHandler;
import com.github.yungyu16.gobang.web.ws.msg.InputMsg;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/12/9.
 */
@WsMsgHandler
public class ChatMsgHandler extends MsgHandler {

    @Override
    public void whenInputMsg(WebSocketSession session, InputMsg inputMsg) throws InvocationTargetException, IllegalAccessException {

    }

    @Override
    public boolean support(String type, String subType) {
        return false;
    }
}
