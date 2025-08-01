package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.common.model.entity.HoldSeller;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卖方持仓 Mapper 接口
 *
 * @author lxd
 * @since 2024-03-06 01:01:51
 */
@Mapper
public interface HoldSellerMapper extends BaseMapper<HoldSeller> {

}
