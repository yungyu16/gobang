package com.github.yungyu16.gobang.core.msg;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.springframework.web.socket.TextMessage;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Data
public class OutputMsg<T> extends WsMsgBase {
    private T data;

    public OutputMsg(String type, String subType, T data) {
        super(type, subType);
        this.data = data;
    }

    public static <T> com.github.yungyu16.gobang.core.ws.msg.OutputMsg<T> of(String msgType, T data) {
        Preconditions.checkNotNull(msgType);
        return new com.github.yungyu16.gobang.core.ws.msg.OutputMsg<>(msgType, data);
    }

    public TextMessage toWsMessage() {
        String json = JSON.toJSONString(this);
        return new TextMessage(json);
    }
}
