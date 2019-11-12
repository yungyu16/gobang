package com.github.yungyu16.gobang.core.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Winner {
    Black(Board.Color.Black),
    White(Board.Color.White),
    /**平局*/
    Draw(null);

    private final Board.Color color;

}
