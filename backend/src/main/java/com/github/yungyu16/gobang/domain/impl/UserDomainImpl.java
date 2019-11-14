package com.github.yungyu16.gobang.domain.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.dao.mapper.UserMapper;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    private LoadingCache<Integer, UserRecord> userRecordCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, UserRecord>() {
                @Override
                public UserRecord load(Integer key) throws Exception {
                    return getById(key);
                }
            });

    @Override
    public Optional<UserRecord> getUserInfoThroughCache(Integer userId) {
        UserRecord userRecord = null;
        try {
            userRecord = userRecordCache.get(userId);
        } catch (ExecutionException e) {
            log.error("查询用户信息异常...", e);
            return Optional.empty();
        }
        return Optional.ofNullable(userRecord);
    }
}
