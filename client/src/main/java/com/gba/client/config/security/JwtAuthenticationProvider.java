package com.gba.client.config.security;

import com.gba.client.model.entity.User;
import com.gba.client.service.UserService;
import com.gba.client.util.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in 2023/12/28
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取用户名和密码
        String username = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());

        // 根据用户名从用户服务中加载用户信息
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 验证密码是否匹配
        if (password.equals(user.getPassword())) {
            // 密码匹配成功，返回认证对象
            UserContextUtil.setUser(user);
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }

        // 密码不匹配，抛出认证异常
        throw new BadCredentialsException("密码不正确");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
