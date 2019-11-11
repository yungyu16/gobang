/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.websocket;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.core.OnlineGameContext;
import com.github.yungyu16.gobang.web.websocket.msg.GameInputMsg;
import com.github.yungyu16.gobang.web.websocket.msg.MsgTypes;
import com.github.yungyu16.gobang.web.websocket.msg.OutputMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameMsgHandler extends TextWebSocketHandler {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private OnlineGameContext onlineGameContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(OutputMsg.of(MsgTypes.USER_MSG_WELCOME, "Welcome~").toTextMessage());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println(">>>>>>>接受到ws消息" + payload);
        GameInputMsg gameInputMsg = JSON.parseObject(payload, GameInputMsg.class);
        try {
            String msgType = gameInputMsg.getMsgType();
            Integer gameId = gameInputMsg.getGameId();
            if (StringTools.isBlank(msgType)) {
                throw new BizException("msgType为空");
            }
            if (gameId == null) {
                throw new BizException("gameId为空");
            }

            switch (msgType) {
                case MsgTypes.GAME_MSG_CHECK_BOARD:

                    break;
                case MsgTypes.GAME_MSG_DISMISS_GAME:

                    break;
                case MsgTypes.GAME_MSG_ENTER_GAME:

                    break;
                default:
                    log.info("不支持的消息类型：{}", msgType);
            }
        } catch (BizException e) {
            log.info("消息处理异常 {}", e.getMessage());
            onlineGameContext.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_TOAST, e.getMessage()));
        } catch (Exception e) {
            log.info("消息处理异常", e);
            onlineGameContext.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_ERROR, e.getMessage()));
        }

    }
}