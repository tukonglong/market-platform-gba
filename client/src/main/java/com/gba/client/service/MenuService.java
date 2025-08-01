package com.gba.client.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.client.model.dto.MenuDTO;
import com.gba.client.model.entity.Menu;
import com.gba.client.model.vo.MenuVO;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单Service
 * </p>
 *
 * @author lxd
 * @since 2024-01-17 05:55:08
 */
public interface MenuService extends IService<Menu> {
    /**
     * 根据用户id获取关联获取菜单
     *
     * @param userId
     * @return
     */
    List<Menu> getMenuByUserId(Long userId);

    /**
     * 根据角色ids获取对应关联菜单(树形结构)
     *
     * @param roleIds
     * @return
     */
    List<MenuVO> getMenuTreeByRoleIds(List<Long> roleIds);

    /**
     * 根据角色ids获取对应关联菜单
     *
     * @param roleIds
     * @return
     */
    List<Menu> getMenuByRoleIds(List<Long> roleIds);

    /**
     * 根据角色id获取关联菜单(树形结构)
     *
     * @param roleId
     * @return
     */
    List<MenuVO> getMenuTreeByRoleId(Long roleId);

    /**
     * 根据角色id获取关联菜单
     *
     * @param roleId
     * @return
     */
    List<Menu> getMenuByRoleId(Long roleId);

    /**
     * 分页
     *
     * @param pageRequest
     * @return
     */
    PageResponse<MenuVO> page(PageRequest<MenuDTO, Menu> pageRequest);

    /**
     * 新增 不传id
     *
     * @param dto
     */
    Long insert(MenuDTO dto);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 明细
     *
     * @param id
     * @return
     */
    MenuVO info(Long id);

    /**
     * 批量更新 id必传
     *
     * @param list
     */
    void updateBatch(List<Menu> list);

    /**
     * getMap
     *
     * @param ids
     * @param columns 查询字段
     * @return key: id, value: entity
     */
    Map<Long, Menu> getMapByIds(List<Long> ids, SFunction<Menu, ?>... columns);

    /**
     * 转为树形结构
     *
     * @param allMenu
     * @param parentId
     * @return
     */
    List<MenuVO> transferMenuVo(List<Menu> allMenu, Long parentId);
}
