package com.github.yungyu16.gobang.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.yungyu16.gobang.base.DbRecordBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

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
@TableName("t_game")
public class GameRecord extends DbRecordBase {

    public static final String BLACK_USER_ID = "black_user_id";

    public static final String WHITE_USER_ID = "white_user_id";

    public static final String WINNER_ID = "winner_id";

    public static final String GAME_START_TIME = "game_start_time";

    public static final String GAME_END_TIME = "game_end_time";

    public static final String GAME_PIC = "game_pic";

    private Integer blackUserId;

    private Integer whiteUserId;

    private Integer winnerId;

    private LocalDateTime gameStartTime;

    private LocalDateTime gameEndTime;

    private byte[] gamePic;
}
