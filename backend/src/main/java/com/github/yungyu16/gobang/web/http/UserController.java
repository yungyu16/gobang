/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import cn.xiaoshidai.common.toolkit.base.DigestTools;
import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yungyu16.gobang.annotation.WithoutLogin;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.model.ReqResult;
import com.github.yungyu16.gobang.web.http.entity.AccountForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserDomain userDomain;

    @GetMapping("validate")
    public void validate() {

    }

    @WithoutLogin
    @PostMapping("sign-in")
    public ReqResult<String> signIn(@RequestBody AccountForm userForm) {
        String mobile = userForm.getMobile();
        String password = userForm.getPassword();

        if (StringTools.isAnyBlank(mobile, password)) {
            throw new BizException("缺少必要参数");
        }
        mobile = mobile.trim();
        password = password.trim();
        log.info("开始登陆....");
        LambdaQueryWrapper<UserRecord> queryWrapper = Wrappers.lambdaQuery(new UserRecord());
        queryWrapper.ge(UserRecord::getMobile, mobile);
        UserRecord user = userDomain.getOne(queryWrapper);
        String digestPwd = getDigestPwd(password);
        if (StringTools.equalsIgnoreCase(digestPwd, user.getPwd())) {
            String sessionToken = newSession(user.getId());
            return ReqResult.success(sessionToken);
        } else {
            throw new BizException("密码错误");
        }
    }

    @WithoutLogin
    @PostMapping("sign-up")
    public synchronized void signUp(@RequestBody AccountForm userForm) {
        String userName = userForm.getUserName();
        String mobile = userForm.getMobile();
        String password = userForm.getPassword();

        if (StringTools.isAnyBlank(userName, mobile, password)) {
            throw new BizException("缺少必要参数");
        }
        userName = userName.trim();
        mobile = mobile.trim();
        password = password.trim();

        checkUserName(userName);
        checkPassword(password);

        LambdaQueryWrapper<UserRecord> queryWrapper = Wrappers.lambdaQuery(new UserRecord());
        queryWrapper.ge(UserRecord::getMobile, mobile);
        int count = userDomain.count(queryWrapper);
        if (count > 0) {
            throw new BizException("手机号码已注册");
        }

        UserRecord entity = new UserRecord();
        entity.setUserName(userName);
        entity.setMobile(mobile);
        entity.setPwd(getDigestPwd(password));
        userDomain.save(entity);
    }

    @GetMapping("detail")
    public void detail() {
    }

    @GetMapping("history")
    public void history() {
    }

    private String getDigestPwd(String password) {
        return DigestTools.md5Hex(password + "gobang");
    }

    private void checkPassword(String password) {
        int length = password.length();
        if (length < 5 || length > 10) {
            throw new BizException("密码不合法");
        }
    }

    private void checkUserName(String userName) {
        if (userName.length() > 20) {
            throw new BizException("用户名太长");
        }
        if (!StringUtils.isAlphanumeric(userName)) {
            throw new BizException("用户名仅支持字母数字");
        }
    }

}
