package com.gba.client.support.Interceptor;

import com.alibaba.fastjson2.JSON;
import com.gba.client.util.UserContextUtil;
import com.gba.common.support.anno.Idempotent;
import com.gba.common.util.Check;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in 2024/5/10
 */
@Component
public class IdempotentInterceptor implements HandlerInterceptor {
    @Autowired
    public RedissonClient redissonClient;

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Idempotent annotation = method.getAnnotation(Idempotent.class);
            if (annotation != null) {
                // 判断是否为重复提交
                Check.checkArgument(this.isNotRepeatSubmit(request, annotation), annotation.message());
            }
            return true;
        }
        return true;
    }

    /**
     * 判断是否重复提交
     *
     * @param request    请求对象
     * @param annotation 幂等注解
     * @return 重复提交请求返回true
     */
    private boolean isNotRepeatSubmit(HttpServletRequest request, Idempotent annotation) throws IOException, NoSuchAlgorithmException {
        // 用户ID + URI为Redis的Key, 请求参数md5为Value
        long userId = UserContextUtil.getUser().getId();
        String uri = request.getRequestURI();
        String key = REPEAT_SUBMIT_KEY + userId + uri;
        RBucket<String> bucket = redissonClient.getBucket(key);

        // 获取请求体参数
        String requestBody = getRequestBody(request);
        if (StringUtils.isBlank(requestBody)) {
            requestBody = JSON.toJSONString(request.getParameterMap());
        }
        // redis查询不为null，并且本次的请求参数md5与val相同则为重复请求
        if (StringUtils.isNotBlank(bucket.get())){
            return !bucket.get().equals(md5(requestBody));
        }

        return bucket.trySet(md5(requestBody), annotation.interval(), TimeUnit.MILLISECONDS);
    }

    /**
     * 读取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getReader());
    }

    /**
     * MD5摘要并转换为字符串
     */
    private static String md5(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] mdBytes = messageDigest.digest(str.getBytes());
        return DatatypeConverter.printHexBinary(mdBytes);
    }
}
