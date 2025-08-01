package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.common.model.entity.CloseBuyer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 买方平仓表 Mapper 接口
 *
 * @author lxd
 * @since 2024-03-06 01:29:06
 */
@Mapper
public interface CloseBuyerMapper extends BaseMapper<CloseBuyer> {

}
