package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.entity.InquiryHistoryInfo;

import java.util.List;
import java.util.Map;

/**
 * 询价历史明细Service
 *
 * @author lxd
 * @since 2024-03-22 01:54:36
 */
public interface InquiryHistoryInfoService extends IService<InquiryHistoryInfo> {

    Map<Long, List<InquiryHistoryInfo>> getMapByHistoryIds(List<Long> historyIds);
}
