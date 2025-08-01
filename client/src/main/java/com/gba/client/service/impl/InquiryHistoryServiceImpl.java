package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.InquiryHistoryMapper;
import com.gba.client.service.InquiryHistoryInfoService;
import com.gba.client.service.InquiryHistoryService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.InquiryHistoryDTO;
import com.gba.common.model.entity.InquiryHistory;
import com.gba.common.model.entity.InquiryHistoryInfo;
import com.gba.common.model.vo.InquiryHistoryInfoVO;
import com.gba.common.model.vo.InquiryHistoryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 询价历史 服务实现类
 *
 * @author lxd
 * @since 2024-03-20 04:45:46
 */
@Service
public class InquiryHistoryServiceImpl extends ServiceImpl<InquiryHistoryMapper, InquiryHistory> implements InquiryHistoryService {
    @Autowired
    private InquiryHistoryInfoService inquiryHistoryInfoService;

    @Override
    public PageResponse<InquiryHistoryVO> pageByLevel1(PageRequest<InquiryHistoryDTO, InquiryHistory> pageRequest) {
        InquiryHistoryDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new InquiryHistoryDTO());

        //动态排序 默认根据id倒序
        String orderBy = "ORDER BY " + (StringUtils.isNotBlank(pageRequest.getSort())
                ? pageRequest.getSortToUnderscore()
                + (pageRequest.getIsDesc() != null && pageRequest.getIsDesc() ? " DESC" : " ASC")
                : "id DESC");

        // 获取当前日期的起始和结束时间
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        //获取当前登录用户角色code
        Page<InquiryHistory> page = lambdaQuery()
                .between(InquiryHistory::getCreateTime, startOfDay, endOfDay)
                .eq(InquiryHistory::getCreator, UserContextUtil.getUserId())
                .last(StringUtils.isNotBlank(orderBy), orderBy)
                .setEntity(dto.toEntity())
                .page(pageRequest.getPage());

        List<InquiryHistoryVO> vos = InquiryHistoryVO.fromEntities(page.getRecords());
        List<Long> ids = vos.stream().map(InquiryHistoryVO::getId).collect(Collectors.toList());
        Map<Long, List<InquiryHistoryInfo>> infoMaps = inquiryHistoryInfoService.getMapByHistoryIds(ids);
        vos.forEach(vo -> {
            List<InquiryHistoryInfoVO> infoVOS = InquiryHistoryInfoVO.fromEntities(Optional.ofNullable(infoMaps.get(vo.getId())).orElse(Collections.emptyList()));
            vo.setInfos(infoVOS.stream().peek(info -> info.setSellerCode("-"))
                    .sorted(Comparator.comparing(InquiryHistoryInfoVO::getExercisePricePercent)
                            .thenComparing(InquiryHistoryInfoVO::getTimeLimit))
                    .collect(Collectors.toList()));

        });

        return PageResponse.build(vos, page.getTotal());
    }
}
