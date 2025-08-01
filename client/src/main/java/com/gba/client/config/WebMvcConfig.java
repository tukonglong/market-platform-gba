package com.gba.client.config;

import com.gba.client.config.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author liuxudong
 * @Date 2024/1/9
 * @Description 
 **/
@Configuration
@RefreshScope
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    UserInterceptor userInterceptor;

    @Value("${white.urls: }")
    private String[] excludePathPatterns;

    /**
     * 请求拦截配置
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截器
        InterceptorRegistration registration = registry.addInterceptor(userInterceptor);
        //registry.addInterceptor(idempotentInterceptor);
        // 所有路径都被拦截
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(excludePathPatterns);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
