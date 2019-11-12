package com.github.yungyu16.gobang.core.game;

import com.github.yungyu16.gobang.core.game.referee.GobangReferee;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/** 一局游戏 */
@Getter
public class GobangGameImpl implements GobangGame {
    private final GobangReferee referee;
    Logger log = LoggerFactory.getLogger(GobangGameImpl.class);
    private BoardImpl board;
    private final Consumer<Winner> gameOverHandler;

    private Board.Color activePlayer;

    private Winner winner = null;

    public GobangGameImpl(int size, GobangReferee referee, Consumer<Winner> gameOverHandler){
        this.board = new BoardImpl(size);
        this.gameOverHandler = gameOverHandler;
        this.activePlayer = Board.Color.Black;
        this.referee = referee;

    }

    @Override
    public boolean move(Board.Color color, int x, int y) {
        if (color!=activePlayer || isGameOver()) {
            log.warn("不是"+color+"的回合！");
            return false;
        }
        if (!board.put(color, new Point(x, y))) {
            log.warn("不可落子的位置");
            return false;
        }

        WinnerMessage winnerMessage = referee.judge(board);
        if (winnerMessage != null) {
            this.winner = winnerMessage.getWinner();
            if (winnerMessage.getMessage() != null) {
                log.info(winnerMessage.getMessage());
            }
            if (gameOverHandler != null) {
                gameOverHandler.accept(winner);
            }
            return true;
        }
        exchangePlayer();
        return true;
    }

    public boolean isGameOver(){
        return winner!=null;
    }

    @Override
    public void cancel(int step) {
        if (step>0) {
            if(isGameOver()){
                winner = null;
            }
            board.cancel(step);
        }
    }

    private void exchangePlayer(){
        activePlayer = Board.Color.exchange(activePlayer);
    }
}
