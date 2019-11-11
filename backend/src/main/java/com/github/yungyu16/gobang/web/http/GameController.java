/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.core.OnlineGameContext;
import com.github.yungyu16.gobang.core.OnlineUserContext;
import com.github.yungyu16.gobang.dao.entity.GameRecord;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.GameDomain;
import com.github.yungyu16.gobang.model.ReqResult;
import com.github.yungyu16.gobang.web.websocket.msg.MsgTypes;
import com.github.yungyu16.gobang.web.websocket.msg.OutputMsg;
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
    private OnlineGameContext onlineGameContext;
    @Autowired
    private OnlineUserContext onlineUserContext;

    @GetMapping("create")
    public ReqResult create() {
        GameRecord entity = new GameRecord();
        gameDomain.save(entity);
        Integer id = entity.getId();
        onlineGameContext.newGame(id);
        return ReqResult.success(id);
    }

    @GetMapping("invite")
    public ReqResult invite(@RequestParam Integer gameId, @RequestParam Integer userId) {
        sendInviteMsg(userId, gameId);
        return ReqResult.success();
    }

    @GetMapping("create-and-invite")
    public ReqResult createAndInvite(@RequestParam Integer userId) {
        GameRecord entity = new GameRecord();
        gameDomain.save(entity);
        Integer gameId = entity.getId();
        onlineGameContext.newGame(gameId);
        sendInviteMsg(userId, gameId);
        return ReqResult.success(gameId);
    }

    private void sendInviteMsg(Integer userId, Integer gameId) {
        UserRecord userRecord = getCurrentUserRecord().orElseThrow(BizSessionTimeOutException::new);
        String userName = userRecord.getUserName();
        JSONObject gameInvitation = new JSONObject();
        gameInvitation.put("gameId", gameId);
        gameInvitation.put("userName", userName);
        onlineUserContext.sendMsg2User(userId, OutputMsg.of(MsgTypes.USER_MSG_INVITE_GAME, gameInvitation));
    }
}
