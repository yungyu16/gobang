package com.github.yungyu16.gobang.domain.impl;

import com.github.yungyu16.gobang.dao.entity.HistoryRecord;
import com.github.yungyu16.gobang.dao.mapper.HistoryMapper;
import com.github.yungyu16.gobang.domain.HistoryDomain;
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
public class HistoryDomainImpl extends ServiceImpl<HistoryMapper, HistoryRecord> implements HistoryDomain {

}
