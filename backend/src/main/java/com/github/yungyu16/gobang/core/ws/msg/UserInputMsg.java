package com.github.yungyu16.gobang.core.ws.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class UserInputMsg extends WsMsg {

    private String sessionToken;

    private String data;

    public JSONArray getJsonArrayData() {
        return JSON.parseArray(data);
    }

    public JSONObject getJsonObjectData() {
        return JSON.parseObject(data);
    }
}
