package com.github.yungyu16.gobang.core.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Point {
    private int x;
    private int y;
    public int getIndex(int size){
        return x + y * size;
    }

    public static Point pointOfIndex(int size, int index){
        if (index >= size * size) {
            throw new IllegalArgumentException();
        }
        return new Point(index%size,index/size);
    }
}
