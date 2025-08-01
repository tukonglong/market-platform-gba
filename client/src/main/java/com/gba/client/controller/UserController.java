package com.gba.client.controller;

import cn.hutool.jwt.JWT;
import com.gba.client.constant.Constant;
import com.gba.client.model.dto.UserDTO;
import com.gba.client.model.entity.User;
import com.gba.client.model.vo.UserVO;
import com.gba.client.service.UserService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.support.anno.RateLimit;
import com.gba.common.util.Check;
import com.gba.common.util.IpUtil;
import com.gba.common.util.RedisDataBase1Utils;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RedisDataBase1Utils redisDataBase1Utils;
    private final AuthenticationManager authenticationManager;

    /**
     * 登录方法
     *
     * @param dto
     * @return JWT令牌
     */
    @RateLimit
    @PostMapping("/login")
    @Operation(summary = "登录")
    public UserVO login(HttpServletRequest request, @RequestBody UserDTO dto) {
        /*ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String captcha = valueOps.get(Constant.CAPTCHA + dto.getCaptchaKey());
        Check.checkNotNull(captcha, "验证码失效, 请重新获取验证码");
        Check.checkNotNull(dto.getCaptcha(), "请输入验证码");
        Check.checkArgument(captcha.equals(dto.getCaptcha()), "验证码不正确");*/
        // 创建认证令牌
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        // 对认证令牌进行验证
        authenticationManager.authenticate(authenticationToken);
        String ip = IpUtil.getClientIp(request);
        String key = UserContextUtil.getUserId() + "_" + ip;
        String token = redisDataBase1Utils.getObject(Constant.TOKEN + key);
        if (StringUtils.isBlank(token)) {
            token = JWT.create()
                    .setPayload("username", dto.getUsername())
                    .setKey(Constant.JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8))
                    .sign();

            redisDataBase1Utils.setObject(Constant.TOKEN + key, token, 7L, TimeUnit.DAYS);
        }

        userService.setLoginTime();
        return userService.infoByCurrentUser().setToken(token).setLastIp(ip);
    }

    @GetMapping("/loginByTel")
    @Operation(summary = "通过手机号登录")
    public UserVO loginByTel(HttpServletRequest request, @RequestParam String phoneNumber, @RequestParam String code) {
        String key = Constant.VERIFICATION + phoneNumber;
        String verificationCode = redisDataBase1Utils.getObject(key);
        Check.checkArgument(StringUtils.isNotBlank(code), "请输入验证码");
        Check.checkArgument(StringUtils.isNotBlank(verificationCode) && verificationCode.equals(code), "验证码错误");

        User user = userService.getUserByPhoneNumber(phoneNumber);
        Check.checkNotNull(user, "该号码未注册");

        // 创建认证令牌
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        // 对认证令牌进行验证
        authenticationManager.authenticate(authenticationToken);
        String ip = IpUtil.getClientIp(request);
        String tokenKey = UserContextUtil.getUserId() + "_" + ip;
        String token = redisDataBase1Utils.getObject(tokenKey);
        if (StringUtils.isBlank(token)) {
            token = JWT.create()
                    .setExpiresAt(new Date(System.currentTimeMillis()))
                    .setPayload("username", user.getUsername())
                    .setKey(Constant.JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8))
                    .sign();
            redisDataBase1Utils.setObject(Constant.TOKEN + tokenKey, token, 7L, TimeUnit.DAYS);
        }

        return userService.infoByCurrentUser().setToken(token);
    }

    @RateLimit
    @Operation(summary = "修改密码")
    @PostMapping("/changePassword")
    public void changePassword(HttpServletRequest request, @RequestBody UserDTO dto) {
        User user = userService.getUserByUsername(dto.getUsername());
        Check.checkNotNull(user, "账号输入错误");
        Check.checkArgument(!dto.getOldPassword().equals(dto.getNewPassword()), "新旧密码不能一样");
        String captcha = redisDataBase1Utils.getObject(Constant.CAPTCHA + dto.getCaptchaKey());
        Check.checkNotNull(captcha, "验证码失效, 请重新获取验证码");
        Check.checkNotNull(dto.getCaptcha(), "请输入验证码");
        Check.checkArgument(captcha.equals(dto.getCaptcha()), "验证码不正确");

        user.setPassword(dto.getNewPassword());
        UserContextUtil.setUser(user);
        userService.updateById(user);
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String ip = IpUtil.getClientIp(request);
        redisDataBase1Utils.deleteObject(Constant.TOKEN + UserContextUtil.getUserId() + "_" + ip);
        // 清除安全上下文
        SecurityContextHolder.clearContext();
        // 使当前会话无效
        request.getSession().invalidate();
    }

    @GetMapping("info/{id}")
    @Operation(summary = "查询详情")
    public UserVO info(@PathVariable Long id) {
        return userService.info(id);
    }

    @GetMapping("infoByCurrentUser")
    @Operation(summary = "获取当前登录用户详情")
    public UserVO infoByCurrentUser() {
        return userService.infoByCurrentUser();
    }

    @GetMapping("sendVerificationCode")
    @Operation(summary = "发送验证码")
    public void getVerificationCode(@RequestParam String phoneNumber) {
        User user = userService.getUserByPhoneNumber(phoneNumber);
        Check.checkNotNull(user, "该号码未注册");

        String key = Constant.COOL_DOWN + phoneNumber;
        // 检查是否处于冷却期内
        Check.checkArgument(!redisDataBase1Utils.hasKey(key), "操作太频繁，请稍后再试");

        // 生成验证码
        String verificationCode = generateCode();
        redisDataBase1Utils.setObject(Constant.VERIFICATION + phoneNumber, verificationCode, 5L, TimeUnit.MINUTES); // 保存验证码，有效期5分钟
        redisDataBase1Utils.setObject(key, key, 60L, TimeUnit.SECONDS); // 设置冷却期

        // 阿里云发送短信
        // AliSmsSender.send(phoneNumber, verificationCode);
    }

    @GetMapping("/getCoolDownTime")
    @Operation(summary = "获取短信验证码冷却时间")
    public Long getCoolDownTime(@RequestParam String phoneNumber) {
        String key = Constant.COOL_DOWN + phoneNumber;
        long coolDownTimeLeft = redisDataBase1Utils.getExpire(key);
        return coolDownTimeLeft > 0 ? coolDownTimeLeft : 0;
    }

    public static String generateCode() {
        Random random = new Random();
        int number = random.nextInt(1000000); // 生成0到999999之间的随机数
        return String.format("%06d", number); // 格式化为6位数，不足前面补0
    }

    @RateLimit
    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码")
    public Map<String, String> captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String key = UUID.randomUUID().toString();
        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(115, 30);
        captcha.setFont(Captcha.FONT_2);
        redisDataBase1Utils.setObject(Constant.CAPTCHA + key, captcha.text(), 5L, TimeUnit.MINUTES);
        Map<String, String> map = new HashMap<>();
        map.put("captchaKey", key);
        map.put("captcha", captcha.toBase64());
        return map;
    }
}
