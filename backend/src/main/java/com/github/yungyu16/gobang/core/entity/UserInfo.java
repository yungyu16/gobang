package com.github.yungyu16.gobang.core.entity;

import com.github.yungyu16.gobang.dao.entity.UserRecord;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Data
public class UserInfo {

    private WebSocketSession socketSession;

    private UserRecord userRecord;

    public UserInfo(WebSocketSession socketSession, UserRecord userRecord) {
        this.socketSession = socketSession;
        this.userRecord = userRecord;
    }
}
