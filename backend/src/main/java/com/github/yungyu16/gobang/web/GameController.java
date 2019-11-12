/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web;

import com.alibaba.fastjson.JSONObject;
import com.github.yungyu16.gobang.base.WebRespBase;
import com.github.yungyu16.gobang.core.game.gobang.GobangContext;
import com.github.yungyu16.gobang.core.OnlineUserContext;
import com.github.yungyu16.gobang.dao.entity.GameRecord;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.GameDomain;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import com.github.yungyu16.gobang.core.ws.msg.MsgTypes;
import com.github.yungyu16.gobang.core.ws.msg.OutputMsg;
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
    private GobangContext gobangContext;

    @Autowired
    private OnlineUserContext onlineUserContext;

    @GetMapping("create")
    public WebRespBase create() {
        UserRecord userRecord = getCurrentUserRecord().orElseThrow(BizSessionTimeoutException::new);
        gobangContext.userGame(userRecord.getId())
                .ifPresent(it -> {
                    Integer gameRole = it.getGameRole();
                    if (gameRole != 3) {
                        throw new BizException("当前有未完成的比赛...");
                    }
                });

        GameRecord entity = new GameRecord();
        gameDomain.save(entity);
        Integer id = entity.getId();
        gobangContext.newGame(id);
        return WebRespBase.success(id);
    }

    @GetMapping("invite")
    public WebRespBase invite(@RequestParam Integer gameId, @RequestParam Integer userId) {
        sendInviteMsg(userId, gameId);
        return WebRespBase.success();
    }

    @GetMapping("create-and-invite")
    public WebRespBase createAndInvite(@RequestParam Integer userId) {
        UserRecord userRecord = getCurrentUserRecord().orElseThrow(BizSessionTimeoutException::new);
        gobangContext.userGame(userRecord.getId())
                .ifPresent(it -> {
                    Integer gameRole = it.getGameRole();
                    if (gameRole != 3) {
                        throw new BizException("当前有未完成的比赛...");
                    }
                });
        GameRecord entity = new GameRecord();
        gameDomain.save(entity);
        Integer gameId = entity.getId();
        gobangContext.newGame(gameId);
        sendInviteMsg(userId, gameId);
        return WebRespBase.success(gameId);
    }

    private void sendInviteMsg(Integer userId, Integer gameId) {
        UserRecord userRecord = getCurrentUserRecord().orElseThrow(BizSessionTimeoutException::new);
        String userName = userRecord.getUserName();
        JSONObject gameInvitation = new JSONObject();
        gameInvitation.put("gameId", gameId);
        gameInvitation.put("userName", userName);
        onlineUserContext.sendMsg2User(userId, OutputMsg.of(MsgTypes.USER_MSG_INVITE_GAME, gameInvitation));
    }
}
