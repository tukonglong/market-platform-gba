package com.gba.client.support.aspect;

import com.gba.client.model.entity.Menu;
import com.gba.client.service.MenuService;
import com.gba.client.support.anno.CheckPermissions;
import com.gba.client.util.UserContextUtil;
import com.gba.common.util.Check;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in 2024/1/19
 */
@Aspect
@Component
public class CheckPermissionsAspect {
    @Autowired
    MenuService menuService;

    @Pointcut("@annotation(com.gba.client.support.anno.CheckPermissions)")
    public void checkPermissions() {
    }

    @Before("checkPermissions()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        //获取方法上有CheckPermissions注解的参数
        Class<?> clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        Method method = clazz.getMethod(methodName, parameterTypes);
        if (method.getAnnotation(CheckPermissions.class) != null) {
            CheckPermissions annotation = method.getAnnotation(CheckPermissions.class);
            String menuCode = annotation.value();
            Check.checkArgument(StringUtils.isNotBlank(menuCode), "接口权限未配置标识");
            List<Menu> menus = menuService.getMenuByUserId(UserContextUtil.getUserId());
            Check.checkArgument(menus.stream().map(Menu::getCode).distinct().anyMatch(code -> code.equals(menuCode)), "无访问权限");
        }
    }
}
