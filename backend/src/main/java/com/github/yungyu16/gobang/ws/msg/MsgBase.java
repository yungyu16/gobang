package com.github.yungyu16.gobang.ws.msg;

import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Data
public class MsgBase {
    private String type;
    private String subType;

    protected MsgBase(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }
}
