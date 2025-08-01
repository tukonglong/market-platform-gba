package com.gba.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gba.common.model.entity.InquiryHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 询价历史 Mapper 接口
 *
 * @author lxd
 * @since 2024-03-20 04:45:46
 */
@Mapper
public interface InquiryHistoryMapper extends BaseMapper<InquiryHistory> {

}
