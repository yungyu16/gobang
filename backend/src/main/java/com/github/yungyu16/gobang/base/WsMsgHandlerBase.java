package com.github.yungyu16.gobang.base;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.gobang.core.WsSocketOperations;
import com.github.yungyu16.gobang.core.msg.WsMsgBase;
import com.github.yungyu16.gobang.core.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.core.ws.msg.OutputMsg;
import com.github.yungyu16.gobang.exeception.BizException;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Slf4j
public abstract class WsMsgHandlerBase<T extends WsMsgBase> extends TypeToken<T> implements WebSocketHandler {

    @Autowired
    private WsSocketOperations wsSocketOperations;
    private BizException UN_SUPPORT_MSG_TYPE = new BizException("不支持的消息类型");
    private Class<T> msgType = (Class<T>) getType();
    private Map<String, Object> msgHandlers = Maps.newConcurrentMap();

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
                T msg = JSON.parseObject(payload, msgType);
                String type = msg.getType();
                String subType = msg.getSubType();
                if (StringUtils.isAnyBlank(type, subType)) {
                    throw new BizException("缺少msgType参数");
                }
                String msgType = String.join(":", type.trim(), subType.trim());
                log.info("开始处理msg:{}", msgType);
                Object o = msgHandlers.get(msgType);

            } else {
                throw UN_SUPPORT_MSG_TYPE;
            }
        } catch (BizException e) {
            log.info("消息处理失败 {}", e.getMessage());
            wsSocketOperations.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_TOAST, e.getMessage()).toTextMessage());
        } catch (Exception e) {
            log.info("消息处理异常", e);
            wsSocketOperations.sendMsg(session, OutputMsg.of(MsgTypes.USER_MSG_ERROR, e.getMessage()).toTextMessage());
        }
    }

    protected abstract void handleTextMessage(WebSocketSession session, T msg);

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
