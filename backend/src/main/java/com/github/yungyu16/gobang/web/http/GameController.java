/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.base.WebResponse;
import com.github.yungyu16.gobang.core.OnlineUserContext;
import com.github.yungyu16.gobang.core.SessionOperations;
import com.github.yungyu16.gobang.dao.entity.GameRecord;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.GameDomain;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import com.github.yungyu16.gobang.web.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.web.ws.msg.OutputMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/8.
 */
@RestController
@RequestMapping("game")
public class GameController extends BaseController {

    @Autowired
    private GameDomain gameDomain;
    @Autowired
    private SessionOperations sessionOperations;
    @Autowired
    private OnlineUserContext onlineUserContext;

    @GetMapping("create")
    public WebResponse create() {
        Integer gameId = newGame();
        return WebResponse.success(gameId);
    }

    @GetMapping("invite")
    public WebResponse invite(@RequestParam Integer gameId, @RequestParam Integer userId) {
        sendInviteMsg(userId, gameId);
        return WebResponse.success();
    }

    @GetMapping("create-and-invite")
    public WebResponse createAndInvite(@RequestParam Integer userId) {
        Integer gameId = newGame();
        sendInviteMsg(userId, gameId);
        return WebResponse.success(gameId);
    }

    private Integer newGame() {
        UserRecord userRecord = sessionOperations.getCurrentUserRecord().orElseThrow(BizSessionTimeoutException::new);
        GameRecord entity = new GameRecord();
        gameDomain.save(entity);
        return entity.getId();
    }

    private void sendInviteMsg(Integer userId, Integer gameId) {
        UserRecord userRecord = sessionOperations.getCurrentUserRecord()
                .orElseThrow(BizSessionTimeoutException::new);
        String userName = userRecord.getUserName();
        JSONObject gameInvitation = new JSONObject();
        gameInvitation.put("gameId", gameId);
        gameInvitation.put("userName", userName);
        onlineUserContext.sendMsg(userId, OutputMsg.of(MsgTypes.USER_MSG_INVITE_GAME, gameInvitation).toTextMessage());
    }
}
