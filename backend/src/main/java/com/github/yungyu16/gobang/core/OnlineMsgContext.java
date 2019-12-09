package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.annotation.WsMsgHandler;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.web.ws.msg.InputMsg;
import com.github.yungyu16.gobang.web.ws.msg.OutputMsg;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/12/9.
 */
@Slf4j
@Component
public class OnlineMsgContext {

    @Autowired
    private WsSocketOperations wsSocketOperations;

    @Autowired
    @WsMsgHandler
    private List<MsgHandler> msgHandlers = Lists.newArrayList();

    public void handleMsg(WebSocketSession session, InputMsg inputMsg) {
        try {
            String type = inputMsg.getType();
            String subType = inputMsg.getSubType();
            Optional<MsgHandler> msgHandlerOpt = msgHandlers.stream()
                    .filter(it -> it.support(type, subType))
                    .findFirst();
            if (msgHandlerOpt.isPresent()) {
                msgHandlerOpt.get().whenInputMsg(session, inputMsg);
            } else {
                throw new BizException("不支持的消息类型:" + type + ":" + subType);
            }
        } catch (BizException e) {
            wsSocketOperations.sendMsg(session, OutputMsg.ofError(e.getMessage()).toWsMessage());
        } catch (Exception e) {
            log.error("处理消息异常", e);
            wsSocketOperations.sendMsg(session, OutputMsg.ofError(e.getMessage()).toWsMessage());
        }
    }
}
