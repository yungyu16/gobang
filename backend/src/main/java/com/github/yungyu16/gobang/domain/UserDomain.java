package com.github.yungyu16.gobang.domain;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.yungyu16.gobang.dao.entity.UserRecord;

import java.util.Optional;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Yungyu
 * @since 2019-11-10
 */
public interface UserDomain extends IService<UserRecord> {
    Optional<UserRecord> getUserInfoThroughCache(Integer userId);
}
