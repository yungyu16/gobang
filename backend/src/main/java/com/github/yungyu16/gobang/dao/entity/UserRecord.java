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
@TableName("t_user")
public class UserRecord extends DbRecordBase {

    public static final String MOBILE = "mobile";

    public static final String USER_NAME = "user_name";

    public static final String PWD = "pwd";

    private String mobile;

    private String userName;

    private String pwd;
}
