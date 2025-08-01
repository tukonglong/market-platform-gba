package com.gba.client.config.security;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.gba.client.constant.Constant;
import com.gba.client.model.entity.User;
import com.gba.client.service.UserService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.util.IpUtil;
import com.gba.common.util.RedisDataBase1Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: liuxudong
 * @Description: 权限验证过滤器
 * @Date: Created in 2023/12/28
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisDataBase1Utils redisDataBase1Utils;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomUnauthorizedHandler customUnauthorizedHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的令牌信息
        String authToken = request.getHeader(Constant.AUTH_HEADER);
        if (StringUtils.isBlank(authToken) || "null".equals(authToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JWT jwt = JWTUtil.parseToken(authToken);
          /*  // 验证令牌是否有效
            if (!jwt.setKey(Constant.JWT_SIGN_KEY.getBytes()).verify()) {
                // 令牌无效
                customUnauthorizedHandler.commence(request, response, new SessionAuthenticationException("身份信息已过期, 请登录后重试"));
                return;
            }*/

            // 获取用户名
            final String userName = (String) jwt.getPayload("username");
            // 获取用户详情信息
            User user = userService.getUserByUsername(userName);
            if (user == null) {
                customUnauthorizedHandler.commence(request, response, new SessionAuthenticationException("无效用户凭证"));
                return;
            }

            // 从Redis中获取存储的Token
            String tokenKey = Constant.TOKEN + user.getId() + "_" + IpUtil.getClientIp(request);
            String token = redisDataBase1Utils.getObject(tokenKey);
            if (token == null || !token.equals(authToken)) {
                customUnauthorizedHandler.commence(request, response, new SessionAuthenticationException("授权过期, 请重新登录"));
                return;
            }

            // 获取用户详情信息
            UserDetails userDetails = userService.loadUserByUsername(userName);
            // 创建认证请求对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 设置用户上下文
            UserContextUtil.setUser(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 通过过滤器链继续处理请求
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Token认证失败", e);
            customUnauthorizedHandler.commence(request, response, new SessionAuthenticationException("Token认证失败"));
        }
    }
}
