package com.github.yungyu16.gobang.web.websocket;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.web.websocket.entity.WsInputMsg;
import com.github.yungyu16.gobang.web.websocket.entity.WsOutputMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
public abstract class BaseWsHandler extends TextWebSocketHandler {

    protected Logger log = LoggerFactory.getLogger(getClass());

    public static final String TYPE_WELCOME = "welcome";

    public static final String TYPE_PING = "ping";

    public static final String TYPE_USER_LIST = "userList";

    public static final String TYPE_ERROR = "error";

    public static final String TYPE_TOAST = "toast";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(WsOutputMsg.of(TYPE_WELCOME, "Welcome~").toTextMessage());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println(">>>>>>>接受到ws消息" + payload);
        WsInputMsg wsInputMsg = JSON.parseObject(payload, WsInputMsg.class);
        try {
            String msgType = wsInputMsg.getMsgType();
            if (StringTools.isBlank(msgType)) {
                throw new BizException("msgType为空");
            }
            handlerMsg(session, wsInputMsg);
        } catch (BizException e) {
            log.info("消息处理异常 {}", e.getMessage());
            session.sendMessage(WsOutputMsg.of(TYPE_ERROR, e.getMessage()).toTextMessage());
        } catch (Exception e) {
            log.info("消息处理异常", e);
            session.sendMessage(WsOutputMsg.of(TYPE_ERROR, e.getMessage()).toTextMessage());
        }
    }

    protected abstract void handlerMsg(WebSocketSession session, WsInputMsg inputMsg);
}
