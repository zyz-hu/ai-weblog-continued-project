package com.zhouyuanzhi.ai.robot.utils;

import com.zhouyuanzhi.ai.robot.enums.ResponseCodeEnum;
import com.zhouyuanzhi.ai.robot.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 获取当前登录用户（由 JwtAuthFilter 写入的请求属性）。
 */
public final class UserContext {

    private static final String USER_ATTR = "X-User-Name";

    private UserContext() {
    }

    /**
     * 获取当前用户名，未登录则抛出业务异常。
     */
    public static String requireUser() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Object username = request.getAttribute(USER_ATTR);
        if (username == null) {
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }
        return username.toString();
    }
}
