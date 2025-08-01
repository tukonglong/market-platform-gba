package com.gba.client.config.security;

import com.alibaba.fastjson2.JSON;
import com.gba.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: liuxudong
 * @Description: 认证异常处理器
 * @Date: Created in 2023/12/28
 */
@Component
@Slf4j
public class CustomUnauthorizedHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSON.toJSONString(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), authException.getMessage())));
        response.getWriter().flush();
    }
}