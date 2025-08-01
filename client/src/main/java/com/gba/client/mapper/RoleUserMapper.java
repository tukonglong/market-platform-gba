package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.client.model.entity.RoleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lxd
 * @since 2024-01-16 04:24:23
 */
@Mapper
public interface RoleUserMapper extends BaseMapper<RoleUser> {
    /**
     * 根据用户id 物理删除
     *
     * @param userId
     */
    @Select({"DELETE FROM client_role_user WHERE user_id = #{userId}"})
    void deleteByUserId(@Param("userId") Long userId);
}
