package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.InquiryHistoryInfoMapper;
import com.gba.client.service.InquiryHistoryInfoService;
import com.gba.common.model.entity.InquiryHistoryInfo;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 询价历史明细 服务实现类
 *
 * @author lxd
 * @since 2024-03-22 01:54:36
 */
@Service
public class InquiryHistoryInfoServiceImpl extends ServiceImpl<InquiryHistoryInfoMapper, InquiryHistoryInfo> implements InquiryHistoryInfoService {

    @Override
    public Map<Long, List<InquiryHistoryInfo>> getMapByHistoryIds(List<Long> historyIds) {
        if(CollectionUtils.isEmpty(historyIds)) {
            return Maps.newConcurrentMap();
        }

       return lambdaQuery()
                .in(InquiryHistoryInfo::getHistoryId, historyIds)
                .list()
                .stream()
                .collect(Collectors.groupingBy(InquiryHistoryInfo::getHistoryId));
    }
}
