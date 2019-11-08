/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@RestController
@RequestMapping("account")
public class AccountController {

    @RequestMapping("sign-up")
    public void signUp(@RequestBody String userName) {
        if (StringTools.isBlank(userName)) {
            throw new BizException("用户名为空");

        }

    }

    @RequestMapping("history")
    public void history() {
    }

}
