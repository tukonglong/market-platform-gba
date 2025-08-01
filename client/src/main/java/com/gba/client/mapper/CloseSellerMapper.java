package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.common.model.entity.CloseSeller;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卖方平仓 Mapper 接口
 *
 * @author lxd
 * @since 2024-03-06 05:07:34
 */
@Mapper
public interface CloseSellerMapper extends BaseMapper<CloseSeller> {

}
