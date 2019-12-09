/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web.http;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yungyu16.gobang.annotation.WithoutLogin;
import com.github.yungyu16.gobang.base.WebResponse;
import com.github.yungyu16.gobang.core.SessionOperations;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.event.SignOutEvent;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.web.http.entity.UserForm;
import com.github.yungyu16.gobang.web.http.entity.UserVO;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/7.
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserDomain userDomain;
    @Autowired
    private SessionOperations sessionOperations;

    private Predicate<Character> userNameMatcher = CharMatcher.inRange('\u4e00', '\u9fa5')
            .or(Character::isLetterOrDigit);

    @GetMapping("validate")
    public WebResponse validate() {
        return WebResponse.success();
    }

    @WithoutLogin
    @PostMapping("sign-in")
    public WebResponse<String> signIn(@RequestBody UserForm userForm) {
        String mobile = userForm.getMobile();
        String password = userForm.getPassword();

        if (StringUtils.isAnyBlank(mobile, password)) {
            throw new BizException("缺少必要参数");
        }
        mobile = mobile.trim();
        password = password.trim();
        checkMobile(mobile);
        log.info("开始登陆....");
        LambdaQueryWrapper<UserRecord> queryWrapper = Wrappers.lambdaQuery(new UserRecord());
        queryWrapper.eq(UserRecord::getMobile, mobile);
        UserRecord user = userDomain.getOne(queryWrapper);
        String digestPwd = getDigestPwd(password);
        if (StringUtils.equalsIgnoreCase(digestPwd, user.getPwd())) {
            String sessionToken = sessionOperations.newSession(user.getId(), mobile);
            return WebResponse.success(sessionToken);
        } else {
            throw new BizException("密码错误");
        }
    }

    @WithoutLogin
    @PostMapping("sign-up")
    public synchronized WebResponse signUp(@RequestBody UserForm userForm) {
        String userName = userForm.getUserName();
        String mobile = userForm.getMobile();
        String password = userForm.getPassword();

        if (StringUtils.isAnyBlank(userName, mobile, password)) {
            throw new BizException("缺少必要参数");
        }
        userName = userName.trim();
        mobile = mobile.trim();
        password = password.trim();

        checkMobile(mobile);
        checkUserName(userName);
        checkPassword(password);

        LambdaQueryWrapper<UserRecord> queryWrapper = Wrappers.lambdaQuery(new UserRecord());
        queryWrapper.eq(UserRecord::getMobile, mobile);
        int count = userDomain.count(queryWrapper);
        if (count > 0) {
            throw new BizException("手机号码已注册");
        }

        UserRecord entity = new UserRecord();
        entity.setUserName(userName);
        entity.setMobile(mobile);
        entity.setPwd(getDigestPwd(password));
        userDomain.save(entity);
        return WebResponse.success();
    }

    @GetMapping("detail")
    public WebResponse<UserVO> detail() {
        return sessionOperations.getSessionUserId()
                .map(it -> {
                    UserRecord userRecord = userDomain.getById(it);
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(userRecord, userVO);
                    return userVO;
                }).map(WebResponse::success)
                .orElseThrow(() -> new BizException("用户不存在"));
    }

    @GetMapping("sign-out")
    public WebResponse signOut() {
        sessionOperations.getSessionToken()
                .ifPresent(it -> {
                    log.info("删除会话...");
                    sessionOperations.removeSession(it);
                    sessionOperations.getSessionUserId().ifPresent(userId -> {
                        applicationContext.publishEvent(new SignOutEvent(userId));
                    });
                });
        return WebResponse.success();
    }

    @GetMapping("history")
    public WebResponse history() {
        return WebResponse.success();
    }

    private String getDigestPwd(String password) {
        return DigestUtils.md5DigestAsHex((password + "gobang").getBytes(StandardCharsets.UTF_8));
    }

    private void checkMobile(String mobile) {
        int length = mobile.length();
        if (length != 11) {
            throw new BizException("手机号码不合法");
        }
        if (!StringUtils.isNumeric(mobile)) {
            throw new BizException("手机号码不合法");
        }
    }

    private void checkPassword(String password) {
        int length = password.length();
        if (length < 5 || length > 10) {
            throw new BizException("密码不合法");
        }
        if (!StringUtils.isAlphanumeric(password)) {
            throw new BizException("密码仅支持字母数字");
        }
    }

    private void checkUserName(String userName) {
        if (userName.length() > 6) {
            throw new BizException("用户名太长");
        }
        for (char ch : userName.toCharArray()) {
            if (!userNameMatcher.test(ch)) {
                throw new BizException("用户名仅支持汉字、字母、数字组合");
            }
        }
    }
}
