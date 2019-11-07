/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web;

import org.springframework.util.StringUtils;
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
        if (StringUtils.isEmpty(userName)) {

        }

    }

    @RequestMapping("history")
    public void history() {
    }

}
