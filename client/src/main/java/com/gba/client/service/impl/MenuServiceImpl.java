package com.gba.client.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.MenuMapper;
import com.gba.client.model.dto.MenuDTO;
import com.gba.client.model.entity.Menu;
import com.gba.client.model.vo.MenuVO;
import com.gba.client.service.MenuService;
import com.gba.client.service.RoleMenuService;
import com.gba.client.service.RoleUserService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.util.Check;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2024-01-17 05:55:08
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleUserService roleUserService;

    @Override
    public List<Menu> getMenuByUserId(Long userId) {
        List<Long> roleIds = roleUserService.getRoleIdsByUserId(userId);
        return getMenuByRoleIds(roleIds);
    }

    @Override
    public List<MenuVO> getMenuTreeByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }

        Map<Long, List<Long>> MenuIdMap = roleMenuService.getMenuIdsByRoleIds(roleIds);
        List<Long> menuIds = MenuIdMap.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(menuIds)) {
            return Lists.newArrayList();
        }

        List<Menu> menus = lambdaQuery().in(Menu::getId, menuIds).eq(Menu::getStatus, Menu.Status.ENABLE).list();
        List<MenuVO> resultList = transferMenuVo(menus, 0L);
        return resultList;
    }

    @Override
    public List<Menu> getMenuByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }

        Map<Long, List<Long>> MenuIdMap = roleMenuService.getMenuIdsByRoleIds(roleIds);
        List<Long> menuIds = MenuIdMap.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(menuIds)) {
            return Lists.newArrayList();
        }

        return lambdaQuery()
                .in(Menu::getId, menuIds)
                .eq(Menu::getStatus, Menu.Status.ENABLE)
                .orderByAsc(Menu::getSort)
                .list();
    }

    @Override
    public List<MenuVO> getMenuTreeByRoleId(Long roleId) {
        if (roleId == null) {
            return Lists.newArrayList();
        }

        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        if (CollectionUtils.isEmpty(menuIds)) {
            return Lists.newArrayList();
        }

        List<Menu> menus = lambdaQuery().in(Menu::getId, menuIds).eq(Menu::getStatus, Menu.Status.ENABLE).list();
        List<MenuVO> resultList = transferMenuVo(menus, 0L);
        return resultList;
    }

    @Override
    public List<Menu> getMenuByRoleId(Long roleId) {
        if (roleId == null) {
            return Lists.newArrayList();
        }

        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        if (CollectionUtils.isEmpty(menuIds)) {
            return Lists.newArrayList();
        }

        return lambdaQuery().in(Menu::getId, menuIds).eq(Menu::getStatus, Menu.Status.ENABLE).list();
    }

    @Override
    public PageResponse<MenuVO> page(PageRequest<MenuDTO, Menu> pageRequest) {
        MenuDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new MenuDTO());

        Page<Menu> page = lambdaQuery()
                .between(Objects.nonNull(pageRequest.getStartTime()) && Objects.nonNull(pageRequest.getEndDate()),
                        Menu::getCreateTime,
                        pageRequest.getStartTime(),
                        Optional.ofNullable(pageRequest.getEndDate())
                                .map(endDate -> endDate.plusDays(1))
                                .orElse(null))
                .orderByAsc(Menu::getSort)
                .setEntity(dto.toEntity())
                .page(pageRequest.getPage());

        return PageResponse.build(MenuVO.fromEntities(page.getRecords()), page.getTotal());
    }

    @Override
    public Long insert(MenuDTO menu) {
        if (menu.getPid() == null || menu.getPid() == 0) {
            menu.setLevel(1);//根节点层级为1
            menu.setPath(null);//根节点路径为空
        } else {
            Menu parentMenu = getById(menu.getPid());
            Check.checkNotNull(parentMenu, "未查询到对应的父节点");
            menu.setLevel(parentMenu.getLevel() + 1);
            if (StringUtils.isNotEmpty(parentMenu.getPath())) {
                menu.setPath(parentMenu.getPath() + "," + parentMenu.getId());
            } else {
                menu.setPath(parentMenu.getId().toString());
            }
        }

        save(menu.toEntity());
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public MenuVO info(Long id) {
        return MenuVO.fromEntity(getById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<Menu> list) {
        updateBatchById(list);
    }

    @SafeVarargs
    @Override
    public final Map<Long, Menu> getMapByIds(List<Long> ids, SFunction<Menu, ?>... columns) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return lambdaQuery()
                    .select(columns)
                    .in(Menu::getId, ids)
                    .list().stream()
                    .collect(Collectors.toMap(Menu::getId, Function.identity()));
        }

        return Maps.newConcurrentMap();
    }

    @Override
    public List<MenuVO> transferMenuVo(List<Menu> allMenu, Long parentId) {
        List<MenuVO> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(allMenu)) {
            for (Menu source : allMenu) {
                if (parentId.equals(source.getPid())) {
                    MenuVO menuVo = new MenuVO();
                    BeanUtils.copyProperties(source, menuVo);
                    //递归查询子菜单，并封装信息
                    List<MenuVO> childList = transferMenuVo(allMenu, source.getId());
                    if (!CollectionUtils.isEmpty(childList)) {
                        menuVo.setChildren(childList);
                    }
                    resultList.add(menuVo);
                }
            }
        }
        return resultList;
    }
}