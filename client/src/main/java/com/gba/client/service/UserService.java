package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.client.model.entity.User;
import com.gba.client.model.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author lxd
 * @since 2023-12-28 05:18:32
 */
public interface UserService extends IService<User>, UserDetailsService {
    /**
     * 根据用户名获取用户
     *
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    User getUserByPhoneNumber(String phoneNumber);

    /**
     * 设置登录时间
     */
    void setLoginTime();

    UserVO info(Long id);

    /**
     * 获取当前登录用户详情
     *
     * @return
     */
    UserVO infoByCurrentUser();
}
