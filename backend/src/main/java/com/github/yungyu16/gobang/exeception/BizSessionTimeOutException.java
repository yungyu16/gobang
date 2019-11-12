package com.github.yungyu16.gobang.exeception;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/12.
 */
public class BizSessionTimeOutException extends BizException {

    public BizSessionTimeOutException() {
        super("会话过期,请重新登陆");
    }
}
