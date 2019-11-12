package com.github.yungyu16.gobang.core.game.gobang.entity;

import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/12.
 */
@Data
public class CheckPointInfo {

    private int x;

    private int y;

    private int color;

    CheckPointInfo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
