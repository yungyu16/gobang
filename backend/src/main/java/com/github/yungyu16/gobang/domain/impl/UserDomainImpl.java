package com.github.yungyu16.gobang.domain.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.dao.mapper.UserMapper;
import com.github.yungyu16.gobang.domain.UserDomain;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Yungyu
 * @since 2019-11-10
 */
@Service
public class UserDomainImpl extends ServiceImpl<UserMapper, UserRecord> implements UserDomain {

}
