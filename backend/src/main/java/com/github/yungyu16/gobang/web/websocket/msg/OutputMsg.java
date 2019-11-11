package com.github.yungyu16.gobang.web.websocket.msg;

import cn.xiaoshidai.common.toolkit.base.ConditionTools;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.web.socket.TextMessage;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class OutputMsg<T> extends WsMsg {

    private String msgType;

    private T data;

    private OutputMsg(String msgType, T data) {
        this.msgType = msgType;
        this.data = data;
    }

    public static <T> OutputMsg<T> of(String msgType, T data) {
        ConditionTools.checkNotNull(msgType);
        return new OutputMsg<>(msgType, data);
    }

    public TextMessage toTextMessage() {
        String json = JSON.toJSONString(this);
        return new TextMessage(json);
    }
}
