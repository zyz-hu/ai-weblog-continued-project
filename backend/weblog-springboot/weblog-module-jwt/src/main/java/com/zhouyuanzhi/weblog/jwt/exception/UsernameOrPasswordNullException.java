package com.zhouyuanzhi.weblog.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: zyz
 * @description: 用户名或者密码为空异常
 **/
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}
