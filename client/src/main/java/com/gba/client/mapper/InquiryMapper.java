package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.common.model.entity.Offer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 询价 Mapper 接口
 *
 * @author lxd
 * @since 2024-03-12 10:12:36
 */
@Mapper
public interface InquiryMapper extends BaseMapper<Offer> {

}
