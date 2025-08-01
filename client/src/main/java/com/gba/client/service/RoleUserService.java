package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.client.model.entity.RoleUser;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Service
 * </p>
 *
 * @author lxd
 * @since 2024-01-16 04:24:23
 */
public interface RoleUserService extends IService<RoleUser> {
    /**
     * 根据用户id获取角色ids
     *
     * @param userId
     * @return
     */
    List<Long> getRoleIdsByUserId(long userId);

    /**
     * 根据用户ids获取对应角色ids
     *
     * @param userIds
     * @return
     */
    Map<Long, List<Long>> getRoleIdsByUserIds(List<Long> userIds);

    /**
     * 根据角色id获取用户ids
     *
     * @param roleId
     * @return
     */
    List<Long> getUserIdsByRoleId(long roleId);
}
