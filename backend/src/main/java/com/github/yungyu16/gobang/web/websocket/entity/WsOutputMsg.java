package com.github.yungyu16.gobang.web.websocket.entity;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.web.socket.TextMessage;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class WsOutputMsg<T> {

    private String msgType;

    private T data;

    private WsOutputMsg(String msgType, T data) {
        this.msgType = msgType;
        this.data = data;
    }

    public static <T> WsOutputMsg<T> of(String msgType, T data) {
        ConditionTools.checkNotNull(msgType);
        return new WsOutputMsg<>(msgType, data);
    }

    public TextMessage toTextMessage() {
        String json = JSON.toJSONString(this);
        return new TextMessage(json);
    }
}
