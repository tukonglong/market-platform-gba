package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.client.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户端用户信息 Mapper 接口
 * </p>
 *
 * @author lxd
 * @since 2023-12-30 08:51:08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
