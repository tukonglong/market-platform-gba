package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.client.model.entity.Role;
import com.gba.client.model.vo.RoleVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户端角色Service
 * </p>
 *
 * @author lxd
 * @since 2024-01-10 10:52:14
 */
public interface RoleService extends IService<Role> {

    Map<Long, Role> getRoleAll();

    List<RoleVO> getRolesByUserId(long userId);

    Map<Long, List<RoleVO>> getRolesByUserIds(List<Long> userIds);
}
