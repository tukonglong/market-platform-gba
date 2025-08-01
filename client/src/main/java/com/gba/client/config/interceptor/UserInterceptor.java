package com.gba.client.config.interceptor;

import com.gba.client.service.UserService;
import com.gba.client.util.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author liuxudong
 * @Date 2024/1/9
 * @Description 请求拦截器
 **/
@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        return true;
    }

    /**
     * 避免内存泄露
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        // 在请求处理完成后清理用户数据
        UserContextUtil.clearUserInfo();
    }
}
