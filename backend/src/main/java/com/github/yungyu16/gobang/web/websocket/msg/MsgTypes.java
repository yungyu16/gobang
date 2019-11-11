package com.github.yungyu16.gobang.web.websocket.msg;

/**
 * CreatedDate: 2019/11/11
 * Author: songjialin
 */
public interface MsgTypes {
    String GAME_MSG_ENTER_GAME = "enterGame";
    String GAME_MSG_PREPARE_GAME = "prepareGame";
    String GAME_MSG_CHECK_BOARD = "checkBoard";
    String GAME_MSG_DISMISS_GAME = "dismissGame";
    String GAME_MSG_INIT_GAME = "initGame";
    String GAME_MSG_START_GAME = "startGame";
    String GAME_MSG_GAME_OVER = "gameOver";
    String USER_MSG_PING = "ping";
    String USER_MSG_USER_LIST = "userList";
    String USER_MSG_WELCOME = "welcome";
    String USER_MSG_INVITE_GAME = "inviteGame";
    String USER_MSG_UNFINISHED_GAME = "unfinishedGame";
    String USER_MSG_ERROR = "error";
    String USER_MSG_TOAST = "toast";
}
