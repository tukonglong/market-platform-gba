package com.gba.client.support.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @Author: liuxudong
 * @Description: 全局字符串trim
 * @Date: Created in 2024/6/25
 */
@Aspect
@Component
public class TrimStringAspect {

    /**
     * 在方法执行前，对传入参数中的字符串字段进行trim操作。
     * 通过使用AOP（面向切面编程）的@Before注解，此方法将在匹配的方法执行前被调用。
     * @Before注解中的表达式"execution(* com.gba.client.controller..*(..))"指定了切面的生效范围，即com.gba.client.controller包及其子包下的所有方法。
     *
     * @param joinPoint 切点对象，包含关于目标方法和参数的信息。使用getArgs()方法可以获取方法的所有参数。
     * @throws IllegalAccessException 如果在访问字段时遇到权限问题，此异常将被抛出。
     */
    @Before("execution(* com.gba.client.controller..*(..))")
    public void trimStrings(JoinPoint joinPoint) throws IllegalAccessException {
        // 获取目标方法的所有参数
        Object[] args = joinPoint.getArgs();
        // 遍历参数数组，对每个参数进行处理
        for (Object arg : args) {
            // 如果参数不为空，则对其进行trim操作
            if (arg != null) {
                trimStringFields(arg);
            }
        }
    }

    /**
     * 对给定对象的所有字符串字段进行修剪操作。
     * 该方法会递归处理对象中的所有字段，如果字段是字符串类型，则调用trim()方法去除前后的空格；
     * 如果字段是自定义的非静态、非最终类的对象，则递归调用本方法处理该对象。
     * <p>
     * 注意：该方法会修改原对象的字段值，不会返回新对象。
     *
     * @param obj 需要处理的对象
     * @throws IllegalAccessException 如果访问字段时没有足够的权限
     */
    private void trimStringFields(Object obj) throws IllegalAccessException {
        // 获取对象的所有字段
        Field[] fields = obj.getClass().getDeclaredFields();
        // 遍历所有字段
        for (Field field : fields) {
            // 判断字段是否为字符串类型且非静态、非最终
            // 检查字段是否为 String 类型，并且不是 static final
            if (field.getType().equals(String.class) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                // 设置字段可访问
                field.setAccessible(true);
                // 获取字段值并进行修剪
                String value = (String) field.get(obj);
                if (value != null) {
                    // 如果字段值非空，则进行修剪并设置回原对象
                    field.set(obj, value.trim());
                }
            } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java") && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                // 如果字段是自定义的非原始类型、非java基础类型、非静态、非最终，则递归处理该字段
                // 如果是嵌套的自定义对象，并且不是 static final，则递归处理
                field.setAccessible(true);
                Object nestedObject = field.get(obj);
                if (nestedObject != null) {
                    // 递归调用本方法处理嵌套对象
                    trimStringFields(nestedObject);
                }
            }
        }
    }

}

