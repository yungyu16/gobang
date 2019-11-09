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
@TableName("t_user")
public class UserRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String mobile;

    private String userName;

    private String pwd;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private String remark;


    public static final String ID = "id";

    public static final String MOBILE = "mobile";

    public static final String USER_NAME = "user_name";

    public static final String PWD = "pwd";

    public static final String IS_DELETED = "is_deleted";

    public static final String CREATE_TIME = "create_time";

    public static final String MODIFY_TIME = "modify_time";

    public static final String REMARK = "remark";

}
