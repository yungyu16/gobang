package com.github.yungyu16.gobang.base;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.constant.WsHandlerName;
import com.github.yungyu16.gobang.core.MsgHandler;
import com.github.yungyu16.gobang.core.WsSocketOperations;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.ws.msg.InputMsg;
import com.github.yungyu16.gobang.ws.msg.OutputMsg;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.UUID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public abstract class WsHandlerBase extends LogOperationsBase implements WebSocketHandler {

    @Autowired
    private WsSocketOperations wsSocketOperations;
    private BizException UN_SUPPORT_MSG_TYPE = new BizException("不支持的消息类型");
    private Map<String, MsgHandler> msgHandlers = Maps.newConcurrentMap();

    public abstract WsHandlerName handlerName();

    public void addMsgHandler(String type, String subType, MsgHandler msgHandler) {
        if (StringUtils.isBlank(type)) {
            throw new NullPointerException("type");
        }
        if (StringUtils.isBlank(type)) {
            throw new NullPointerException("subType");
        }
        msgHandlers.put(String.join(":", type, subType), msgHandler);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            MDC.put("traceId", UUID.randomUUID().toString());
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String payload = textMessage.getPayload();
                if (StringUtils.isBlank(payload)) {
                    throw new BizException("消息为空");
                }
                InputMsg msg = JSON.parseObject(payload, InputMsg.class);
                String type = msg.getType();
                String subType = msg.getSubType();
                if (StringUtils.isAnyBlank(type, subType)) {
                    throw new BizException("缺少msgType参数");
                }
                String msgType = String.join(":", type.trim(), subType.trim());
                log.info("开始处理msg:{}", msgType);
                MsgHandler handler = msgHandlers.get(msgType);
                if (handler == null) {
                    throw new BizException("不支持的消息类型");
                }
                handler.whenInputMsg(session, msg);
            } else {
                throw UN_SUPPORT_MSG_TYPE;
            }
        } catch (BizException e) {
            log.info("消息处理失败 {}", e.getMessage());
            wsSocketOperations.sendMsg(session, OutputMsg.ofToast(e.getMessage()).toWsMessage());
        } catch (Exception e) {
            log.info("消息处理异常", e);
            wsSocketOperations.sendMsg(session, OutputMsg.ofError(e.getMessage()).toWsMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


}
