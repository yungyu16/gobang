package com.github.yungyu16.gobang.core.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WinnerMessage {
    public static final WinnerMessage Black = new WinnerMessage(Winner.Black,null);
    public static final WinnerMessage White = new WinnerMessage(Winner.White,null);
    public static final WinnerMessage Draw = new WinnerMessage(Winner.Draw,null);
    private final Winner winner;
    private final String message;
}
