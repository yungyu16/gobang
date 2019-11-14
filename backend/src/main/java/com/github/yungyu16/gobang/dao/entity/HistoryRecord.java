package com.github.yungyu16.gobang.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.yungyu16.gobang.base.DbRecordBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Yungyu
 * @since 2019-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_history")
public class HistoryRecord extends DbRecordBase {

    public static final String USER_ID = "user_id";

    public static final String GAME_ID = "game_id";

    public static final String USER_COLOR = "user_color";

    public static final String IS_WINNER = "is_winner";

    private Integer userId;

    private Integer gameId;

    private Integer userColor;

    private Integer isWinner;
}
