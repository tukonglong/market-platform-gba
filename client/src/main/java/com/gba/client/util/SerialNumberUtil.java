package com.gba.client.util;

import com.gba.common.util.CalculatorUtil;
import com.gba.common.util.RedisDataBase1Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liuxudong
 * @Description: 业务流水号工具
 * @Date: Created in 2022/12/27
 */
@Component
public class SerialNumberUtil {
    @Autowired
    RedisDataBase1Utils redisUtils;

    private static final String KEY = "SERIAL_NUMBER";

    /**
     * 获取流水号 3位业务CODE + 日期(年 + 月 + 日) + 6位自增序列
     *
     * @param type 业务类型
     * @return 流水号
     */
    public synchronized String getSerialNumber(Type type) {
        int increment = redisUtils.incrementAndGet(KEY, CalculatorUtil.getTomorrowSeconds(), TimeUnit.SECONDS);
        return type.code + DateTimeFormatter.ofPattern("yyMMdd").format(LocalDate.now()) + generateNextSerialNumber(increment, 6);
    }

    /**
     * 获取流水号 3位业务CODE + 日期(年 + 月 + 日) + length位自增序列
     *
     * @param type 业务类型
     * @return 流水号
     */
    public synchronized String getSerialNumber(Type type, int length) {
        int increment = redisUtils.incrementAndGet(KEY, CalculatorUtil.getTomorrowSeconds(), TimeUnit.SECONDS);
        return type.code + DateTimeFormatter.ofPattern("yyMMdd").format(LocalDate.now()) + generateNextSerialNumber(increment, length);
    }

    private static final char[] ALLOWED_CHARS = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
    private static final int BASE = ALLOWED_CHARS.length;

    /**
     * 根据指定长度生成流水码
     * 4位流水号可以生成 1,336,335 个流水码
     * 5位流水号可以生成 45,077,015 个流水码
     * 6位流水号可以生成 1,533,917,295 个流水码
     * <p>
     * 生成规则: 24位字母(去除I,O) + [0~9] 10位数字 = 34的length次方 -1
     *
     * @param currentSerialNumber
     * @param length
     * @return
     */
    public static String generateNextSerialNumber(int currentSerialNumber, int length) {
        // 检查长度是否合法
        if (length <= 0 || length > 10) {
            return null; // 长度不合法
        }

        // 检查是否需要进位
        if (currentSerialNumber >= Math.pow(BASE, length)) {
            return null; // 已达到最大流水号，无法再生成
        }

        // 将新的整数值转换为流水号
        return encodeSerialNumber(currentSerialNumber, length);
    }

    private static String encodeSerialNumber(int value, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = length - 1; i >= 0; i--) {
            sb.append(ALLOWED_CHARS[value / (int) Math.pow(BASE, i) % BASE]);
        }
        return sb.toString();
    }

    /**
     * 业务类型 业务缩写+T(TrackingNo)
     */
    @Getter
    @AllArgsConstructor
    public enum Type {
        USER("UST", "用户");

        private String code;
        private String message;
    }
}
