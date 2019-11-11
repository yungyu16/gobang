package com.github.yungyu16.gobang.core;

import com.github.yungyu16.gobang.base.WebSockOperationBase;
import com.github.yungyu16.gobang.domain.GameDomain;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@Component
public class OnlineGameContext extends WebSockOperationBase implements InitializingBean {

    @Autowired
    private GameDomain gameDomain;

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
