package com.gba.client.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.constant.Constant;
import com.gba.client.mapper.UserMapper;
import com.gba.client.model.entity.Menu;
import com.gba.client.model.entity.User;
import com.gba.client.model.vo.RoleVO;
import com.gba.client.model.vo.UserVO;
import com.gba.client.service.MenuService;
import com.gba.client.service.RoleService;
import com.gba.client.service.UserService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.util.Check;
import com.gba.common.util.RedisDataBase1Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2023-12-28 05:35:56
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final RoleService roleService;
    private final MenuService menuService;
    private final RedisDataBase1Utils redisDataBase1Utils;

    @Override
    public UserVO info(Long id) {
        User user = lambdaQuery().eq(User::getId, id).eq(User::getStatus, 0).one();
        Check.checkArgument(user != null, "用户不存在");
        UserVO userVO = UserVO.fromEntity(user);
        List<RoleVO> roleVOS = roleService.getRolesByUserId(id);

        List<Menu> menus = menuService.getMenuByUserId(id);
        List<Menu> onlyMenus = menus.stream()
                .filter(menu -> Menu.Type.MENU.getCode().equals(menu.getType()))
                .collect(Collectors.toList());
        List<String> buttonCodes = CollStreamUtil.toList(
                menus.stream()
                        .filter(menu -> Menu.Type.BUTTON.getCode().equals(menu.getType()))
                        .collect(Collectors.toList()),
                Menu::getCode
        );
        return userVO.setRoles(roleVOS)
                .setMenus(menuService.transferMenuVo(onlyMenus, 0L))
                .setButtonCodes(buttonCodes);
    }

    @Override
    public UserVO infoByCurrentUser() {
        return info(UserContextUtil.getUserId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String key = Constant.USER_DETAIL + username;
        UserDetails userDetails = redisDataBase1Utils.getObject(key);
        if (userDetails == null) {
            userDetails = lambdaQuery().eq(User::getUsername, username).one();
            if (userDetails != null) {
                redisDataBase1Utils.setObject(key, userDetails, 36000L, TimeUnit.SECONDS);
            }
        }
        return userDetails;
    }

    @Override
    public User getUserByUsername(String username) {
        String key = Constant.USER + username;
        User user = redisDataBase1Utils.getObject(key);
        if (user == null) {
            user = lambdaQuery().eq(User::getUsername, username).one();
            if (user != null) {
                redisDataBase1Utils.setObject(key, user, 36000L, TimeUnit.SECONDS);
            }
        }
        return user;
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return lambdaQuery().eq(User::getTel, phoneNumber).one();
    }

    @Override
    public void setLoginTime() {
        lambdaUpdate().set(User::getLastLoginTime, LocalDateTime.now())
                .eq(User::getId, UserContextUtil.getUserId())
                .update();
    }
}
