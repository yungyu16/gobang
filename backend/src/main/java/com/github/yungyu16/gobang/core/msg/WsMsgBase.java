package com.github.yungyu16.gobang.core.msg;

import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Data
public class WsMsgBase {
    private String type;
    private String subType;

    protected WsMsgBase(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }
}
