package com.github.yungyu16.gobang.ws;

import com.github.yungyu16.gobang.base.WsHandlerBase;
import com.github.yungyu16.gobang.core.WsSocketOperations;
import com.github.yungyu16.gobang.exeception.BizException;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Component
public class OnlineMsgHandler extends WsHandlerBase {

    @Autowired
    private WsSocketOperations wsSocketOperations;
    private BizException UN_SUPPORT_MSG_TYPE = new BizException("不支持的消息类型");
    private Map<String, Object> msgHandlers = Maps.newConcurrentMap();


}
