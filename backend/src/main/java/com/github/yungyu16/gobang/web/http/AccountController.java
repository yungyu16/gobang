/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.github.yungyu16.gobang.model.ReqResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@RestController
@RequestMapping("account")
public class AccountController extends BaseController {

    @GetMapping("validate")
    public ReqResult validate(@RequestParam String userToken) {
        if (StringTools.isBlank(userToken)) {
            throw new BizException("用户令牌为空");
        }
        Boolean hasToken = redisTemplate.hasKey(String.join(":", "token", userToken));
        if (hasToken == null) {
            hasToken = false;
        }
        if (!hasToken) {
            return ReqResult.of(1);
        }
        return ReqResult.success();
    }

    @PostMapping("sign-up")
    public synchronized ReqResult<String> signUp(@RequestBody String userName) {
        if (StringTools.isBlank(userName)) {
            throw new BizException("用户名为空");
        }
        userName = userName.trim();
        Boolean isMember = redisSetOperations.isMember(String.join(":", "ACCOUNT", "NAME_SET"), userName);
        if (isMember == null) {
            isMember = false;
        }
        if (isMember) {
            throw new BizException("用户名已存在");
        }
        String userToken = StringTools.timestampUUID();
        setSessionAttr(userToken, USER_NAME, userName);
        return ReqResult.success(userToken);
    }

    @GetMapping("history")
    public void history() {
    }
}
