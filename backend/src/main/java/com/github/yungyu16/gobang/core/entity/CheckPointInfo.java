package com.github.yungyu16.gobang.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/12.
 */
@Data
@AllArgsConstructor
public class CheckPointInfo {

    private int x;

    private int y;

    private int color;

    CheckPointInfo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
