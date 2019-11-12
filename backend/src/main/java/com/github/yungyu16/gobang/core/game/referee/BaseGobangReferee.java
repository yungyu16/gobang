package com.github.yungyu16.gobang.core.game.referee;

import com.github.yungyu16.gobang.core.game.Board;
import com.github.yungyu16.gobang.core.game.PathNode;
import com.github.yungyu16.gobang.core.game.Winner;
import com.github.yungyu16.gobang.core.game.WinnerMessage;

public abstract class BaseGobangReferee implements GobangReferee {
    /**
     * @param overline 是否区分长连禁手 如果区分，下出长连禁手会判断对手胜利
     */
    protected WinnerMessage judge(Board board, boolean overline){
        PathNode last = board.getLatest();
        if (last == null) {
            return null;
        }
        final Board.Color color = last.getColor();
        if (color == Board.Color.Blank) {
            return null;
        }
        int x0 = last.getX();
        int y0 = last.getY();
        int val0 = color.getValue();
        if (overline) {
            int count1;
            int count2;
            int count3;
            int count4;
            //先判断是否有胜利者 如果有则不用管禁手
            if ((count1 = checkTopToBottom(board, x0, y0, val0))==5
                    || (count2 = checkLeftToRight(board, x0, y0, val0))==5
                    || (count3 = checkTopLeftToRightBottom(board, x0, y0, val0))==5
                    || (count4 = checkTopRightToLeftBottom(board, x0, y0, val0))==5) {
                //  注意禁手仅对黑棋使用，因此此处就默认认为五连黑棋胜 长连白棋胜
                return WinnerMessage.Black;
            } else if (count1 >5 || count2>5 || count3>5 || count4>5){
                return new WinnerMessage(Winner.White,"黑棋长连禁手 白棋胜");
            }
        }else {
            if (checkTopToBottom(board,x0,y0,val0)>=5
                    || checkLeftToRight(board,x0,y0,val0)>=5
                    || checkTopLeftToRightBottom(board,x0,y0,val0)>=5
                    || checkTopRightToLeftBottom(board,x0,y0,val0)>=5) {
                return val0 == Board.Color.Black.getValue()?WinnerMessage.Black:WinnerMessage.White;
            }

        }
        if (board.isFull()) {
            return WinnerMessage.Draw; // 平局
        }
        return null;
    }

    private int checkTopLeftToRightBottom(Board board, int x0, int y0,int val0){
        int x = x0;
        int y = y0;
        int val;
        int count = 0;
        while (x>=0 && y >=0){
            val = board.get(x,y).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            x--;
            y--;
        }
        x = x0+1;
        y = y0+1;
        while (x< board.size() && y < board.size()){
            val = board.get(x,y).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            x++;
            y++;
        }
        return count;

    }

    private int checkTopRightToLeftBottom(Board board, int x0, int y0,int val0){
        int x = x0;
        int y = y0;
        int val;
        int count = 0;
        while (x>=0 && y < board.size()){
            val = board.get(x,y).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            x--;
            y++;
        }
        x = x0+1;
        y = y0-1;
        while (x< board.size() && y >=0){
            val = board.get(x,y).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            x++;
            y--;
        }
        return count;

    }

    private int checkTopToBottom(Board board, int x0, int y0,int val0){
        int y = y0;
        int val;
        int count = 0;
        while (y >= 0){
            val = board.get(x0,y).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            y--;
        }
        y = y0+1;
        while (y < board.size()){
            val = board.get(x0,y).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            y++;
        }
        return count;
    }

    private int checkLeftToRight(Board board, int x0, int y0,int val0){
        int x = x0;
        int val;
        int count = 0;
        while (x < board.size()){
            val = board.get(x,y0).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            x++;
        }

        x = x0 - 1;
        while (x >= 0){
            val = board.get(x,y0).getValue();
            if (val!=val0) {
                break;
            }
            count++;
            x--;
        }
        return count;
    }
}
