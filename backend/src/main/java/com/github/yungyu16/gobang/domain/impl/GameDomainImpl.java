package com.github.yungyu16.gobang.domain.impl;

import com.github.yungyu16.gobang.dao.entity.GameRecord;
import com.github.yungyu16.gobang.dao.mapper.GameMapper;
import com.github.yungyu16.gobang.domain.GameDomain;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yungyu
 * @since 2019-11-10
 */
@Service
public class GameDomainImpl extends ServiceImpl<GameMapper, GameRecord> implements GameDomain {

}
