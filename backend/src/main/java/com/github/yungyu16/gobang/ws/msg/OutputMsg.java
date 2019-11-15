package com.github.yungyu16.gobang.ws.msg;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.springframework.web.socket.TextMessage;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Data
public class OutputMsg<T> extends MsgBase {
    private T data;

    public OutputMsg(String type, String subType, T data) {
        super(type, subType);
        this.data = data;
    }

    public static <T> OutputMsg<T> of(String type, String subType, T data) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(subType);
        return new OutputMsg<>(type, subType, data);
    }

    public static OutputMsg ofError(String msg) {
        return new OutputMsg<>(null, null, null);
    }

    public static OutputMsg ofToast(String msg) {
        return new OutputMsg<>(null, null, null);
    }

    public static OutputMsg ofSessionTimeout() {
        return new OutputMsg<>(null, null, null);
    }

    public TextMessage toWsMessage() {
        String json = JSON.toJSONString(this);
        return new TextMessage(json);
    }
}
