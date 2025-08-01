package com.gba.client.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SimpleQuery;
import com.gba.client.mapper.RoleMenuMapper;
import com.gba.client.model.entity.RoleMenu;
import com.gba.client.service.RoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色菜单关系表 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2024-01-18 03:18:40
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    @Override
    public List<Long> getMenuIdsByRoleId(long roleId) {
        return SimpleQuery.list(Wrappers.<RoleMenu>lambdaQuery().eq(RoleMenu::getRoleId, roleId), RoleMenu::getMenuId);
    }

    @Override
    public Map<Long, List<Long>> getMenuIdsByRoleIds(List<Long> roleIds) {
        return lambdaQuery()
                .in(RoleMenu::getRoleId, roleIds)
                .list()
                .stream()
                .collect(Collectors.groupingBy(RoleMenu::getRoleId
                        , Collectors.mapping(RoleMenu::getMenuId, Collectors.toList())));
    }
}
