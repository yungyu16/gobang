/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.github.yungyu16.gobang.annotation.WithoutLogin;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.model.ReqResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController implements InitializingBean {

    @Autowired
    private UserDomain userDomain;

    @GetMapping("validate")
    public void validate() {

    }

    @GetMapping("sign-in")
    public void signIn(@RequestParam String userToken) {

    }

    @WithoutLogin
    @PostMapping("sign-up")
    public synchronized ReqResult<String> signUp(@RequestBody String userName) {
        if (StringTools.isBlank(userName)) {
            throw new BizException("用户名为空");
        }
        userName = userName.trim();
        Boolean isMember = getRedisSetOperations().isMember(String.join(":", "ACCOUNT", "NAME_SET"), userName);
        if (isMember == null) {
            isMember = false;
        }
        if (isMember) {
            throw new BizException("用户名已存在");
        }
        String userToken = StringTools.timestampUUID();
        getRedisSetOperations().add(String.join(":", "ACCOUNT", "NAME_SET"), userName);
        return ReqResult.success(userToken);
    }

    @GetMapping("detail")
    public void detail() {
    }

    @GetMapping("history")
    public void history() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<UserRecord> list = userDomain.list();
        System.out.println(list);
    }
}
