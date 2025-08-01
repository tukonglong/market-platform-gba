package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.client.model.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 角色菜单关系表 Mapper 接口
 * </p>
 *
 * @author lxd
 * @since 2024-01-18 03:18:40
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 根据角色id 物理删除
     *
     * @param roleId
     */
    @Select({"DELETE FROM client_role_menu WHERE role_id = #{roleId}"})
    void deleteByRoleId(@Param("roleId") Long roleId);
}
