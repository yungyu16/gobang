/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/8.
 */
public abstract class LogOperationsBase {

    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    protected ApplicationContext applicationContext;
}
