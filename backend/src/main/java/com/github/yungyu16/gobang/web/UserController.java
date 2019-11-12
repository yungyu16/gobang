/*
 * Copyright (c) 2019 Yungyu  songjialin16@gmail.com. All rights reserved.
 */

package com.github.yungyu16.gobang.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yungyu16.gobang.annotation.WithoutLogin;
import com.github.yungyu16.gobang.base.WebRespBase;
import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import com.github.yungyu16.gobang.event.SessionTokenEvent;
import com.github.yungyu16.gobang.exeception.BizException;
import com.github.yungyu16.gobang.web.entity.UserForm;
import com.github.yungyu16.gobang.web.entity.UserVO;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private ApplicationContext applicationContext;

    private Predicate<Character> userNameMatcher = CharMatcher.inRange('\u4e00', '\u9fa5')
            .or(Character::isLetterOrDigit);

    @GetMapping("validate")
    public WebRespBase validate() {
        return WebRespBase.success();
    }

    @WithoutLogin
    @PostMapping("sign-in")
    public WebRespBase<String> signIn(@RequestBody UserForm userForm) {
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
            //删除旧会话
            deleteOldSession(mobile);
            //新建会话
            String sessionToken = newSession(user.getId(), mobile);
            return WebRespBase.success(sessionToken);
        } else {
            throw new BizException("密码错误");
        }
    }

    @WithoutLogin
    @PostMapping("sign-up")
    public synchronized WebRespBase signUp(@RequestBody UserForm userForm) {
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
        return WebRespBase.success();
    }

    @GetMapping("detail")
    public WebRespBase<UserVO> detail() {
        return getSessionUserId()
                .map(it -> {
                    UserRecord userRecord = userDomain.getById(it);
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(userRecord, userVO);
                    return userVO;
                }).map(WebRespBase::success)
                .orElseThrow(() -> new BizException("用户不存在"));
    }

    @GetMapping("sign-out")
    public WebRespBase signOut() {
        getSessionToken()
                .ifPresent(it -> {
                    log.info("删除会话...");
                    removeSession(it);
                    applicationContext.publishEvent(new SessionTokenEvent(SessionTokenEvent.TYPE_REMOVE, it));
                });
        return WebRespBase.success();
    }

    @GetMapping("history")
    public WebRespBase history() {
        return WebRespBase.success();
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
