package com.github.yungyu16.gobang.core.game;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


public interface Board {
    @Getter
    @AllArgsConstructor
    enum Color{
        Black(1,"黑棋"),White(2,"白棋"),Blank(0,"空白");
        private final int value;
        private final String cn;

        public static Color valueOf(int val){
            switch (val) {
                case 0:
                    return Blank;
                case 1:
                    return Black;
                case 2:
                    return White;
            }
            throw new IllegalArgumentException("color:"+val);
        }

        @Override
        public String toString() {
            return cn;
        }

        public static Color exchange(Color player){
            return player == Black ? White : Black;
        }
    }

    /** 棋盘是否已满 */
    boolean isFull();

    /**
     *
     * @param color 玩家 black or white
     * @param point 落子点位
     * @return <tt>true</tt> 成功 <tt>false</tt>可能是该位置已经有棋子
     */
    boolean put(Color color, Point point);

    /**
     * 悔棋
     * @param step 悔棋步数 （双方总计）
     */
    void cancel(int step);

    /**
     * @return 若没有棋子或者超出棋盘 返回 {@link Board.Color}.Blank
     */
    Color get(int x, int y);

    List<PathNode> getTrace();

    /** 棋盘的大小（边长，默认为15） */
    int size();

    /** 总计步数（棋盘上的棋子数） */
    int steps();

    PathNode getLatest();


}
