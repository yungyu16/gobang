package com.github.yungyu16.gobang.core.game.referee;

import com.github.yungyu16.gobang.core.game.Board;
import com.github.yungyu16.gobang.core.game.WinnerMessage;

/**
 * 无禁手的简单规则裁判
 */
public class SimpleGameReferee extends BaseGobangReferee implements GobangReferee {
    @Override
    public WinnerMessage judge(Board board) {
        return judge(board,false);
    }
}
