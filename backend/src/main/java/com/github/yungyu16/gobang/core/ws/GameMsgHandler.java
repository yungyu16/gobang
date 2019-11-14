/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.core.ws;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.core.game.gobang.GobangContext;
import com.github.yungyu16.gobang.core.ws.msg.GameInputMsg;
import com.github.yungyu16.gobang.core.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.exeception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

public class GameMsgHandler extends TextWebSocketHandler {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private GobangContext gobangContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //gobangContext.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_WELCOME, "Welcome~").toTextMessage());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MDC.put("traceId", UUID.randomUUID().toString());
        String payload = message.getPayload();
        log.info(">>>>>>>game ws接受到ws消息" + payload);
        GameInputMsg gameInputMsg = JSON.parseObject(payload, GameInputMsg.class);
        try {
            String msgType = gameInputMsg.getMsgType();
            String sessionToken = gameInputMsg.getSessionToken();
            session.getAttributes().put("sessionToken", sessionToken);
            Integer gameId = gameInputMsg.getGameId();
            if (StringUtils.isBlank(msgType)) {
                throw new BizException("msgType为空");
            }
            if (StringUtils.isBlank(sessionToken)) {
                throw new BizException("sessionToken为空");
            }
            if (gameId == null) {
                throw new BizException("gameId为空");
            }
            switch (msgType) {
                case MsgTypes.GAME_MSG_ENTER_GAME:
                    gobangContext.enterGame(session, sessionToken, gameId);
                    break;
                case MsgTypes.GAME_MSG_DISMISS_GAME:

                    break;
                case MsgTypes.GAME_MSG_CHECK_BOARD:
                    gobangContext.checkBoardPoint(sessionToken, gameId, gameInputMsg.getJsonObjectData());
                    break;
                default:
                    log.info("不支持的消息类型：{}", msgType);
            }
        } catch (BizException e) {
            log.info("消息处理异常 {}", e.getMessage());
            //gobangContext.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_TOAST, e.getMessage()).toTextMessage());
        } catch (Exception e) {
            log.info("消息处理异常", e);
            //gobangContext.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_ERROR, e.getMessage()).toTextMessage());
        }
    }
}