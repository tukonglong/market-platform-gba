package com.gba.client.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.client.model.entity.RoleMenu;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色菜单关系表Service
 * </p>
 *
 * @author lxd
 * @since 2024-01-18 03:18:40
 */
public interface RoleMenuService extends IService<RoleMenu> {
    /**
     * 根据角色id获取菜单ids
     *
     * @param roleId
     * @return
     */
    List<Long> getMenuIdsByRoleId(long roleId);

    /**
     * 根据角色ids获取对应菜单ids
     *
     * @param roleIds
     * @return
     */
    Map<Long, List<Long>> getMenuIdsByRoleIds(List<Long> roleIds);
}
