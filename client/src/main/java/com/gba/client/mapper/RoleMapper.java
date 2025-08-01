package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.client.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户端角色 Mapper 接口
 * </p>
 *
 * @author lxd
 * @since 2023-12-30 08:48:40
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
