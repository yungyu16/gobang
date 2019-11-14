package com.github.yungyu16.gobang.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/13.
 */
@Getter
@Setter
public abstract class DbRecordBase implements Serializable {
    public static final String ID = "id";
    public static final String IS_DELETED = "is_deleted";
    public static final String CREATE_TIME = "create_time";
    public static final String MODIFY_TIME = "modify_time";
    public static final String REMARK = "remark";
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean isDeleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;
    private String remark;
}
