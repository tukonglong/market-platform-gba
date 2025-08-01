package com.gba.client.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.gba.client.model.entity.User;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户信息获取工具
 *
 * @author LIUXUDONG
 */
@Slf4j
public class UserContextUtil {
    /**
     * 用户信息
     */
    private static final ThreadLocal<User> userThreadLocal = new TransmittableThreadLocal<>();

    public static void setUser(User User) {
        userThreadLocal.set(User);
    }

    public static User getUser() {
        if (userThreadLocal.get() == null) {
            throw new RuntimeException("获取用户信息失败");
        }

        return userThreadLocal.get();
    }

    public static Long getUserId() {
        return getUser().getId();
    }
    public static String getUserName() {
        return getUser().getUsername();
    }

    public static void clearUserInfo() {
        userThreadLocal.remove();
    }
}
