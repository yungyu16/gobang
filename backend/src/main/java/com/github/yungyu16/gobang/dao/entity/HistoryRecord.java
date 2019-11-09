package com.github.yungyu16.gobang.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
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
public class HistoryRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer userId;

    private Integer gameId;

    private Integer userColor;

    private Integer isWinner;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private String remark;


    public static final String ID = "id";

    public static final String USER_ID = "user_id";

    public static final String GAME_ID = "game_id";

    public static final String USER_COLOR = "user_color";

    public static final String IS_WINNER = "is_winner";

    public static final String IS_DELETED = "is_deleted";

    public static final String CREATE_TIME = "create_time";

    public static final String MODIFY_TIME = "modify_time";

    public static final String REMARK = "remark";

}
