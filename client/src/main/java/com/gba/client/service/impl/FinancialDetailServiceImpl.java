package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.FinancialDetailMapper;
import com.gba.client.service.FinancialDetailService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.FinancialDetailDTO;
import com.gba.common.model.entity.FinancialDetail;
import com.gba.common.model.vo.FinancialDetailVO;
import com.gba.common.util.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 财务流水 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2024-02-29 02:19:01
 */
@Service
public class FinancialDetailServiceImpl extends ServiceImpl<FinancialDetailMapper, FinancialDetail> implements FinancialDetailService {
    @Autowired
    ExcelUtil excelUtil;

    @Override
    public PageResponse<FinancialDetailVO> page(PageRequest<FinancialDetailDTO, FinancialDetail> pageRequest) {
        FinancialDetailDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new FinancialDetailDTO());

        //动态排序 默认根据id倒序
        String orderBy = "ORDER BY " + (StringUtils.isNotBlank(pageRequest.getSort())
                ? pageRequest.getSortToUnderscore()
                + (pageRequest.getIsDesc() != null && pageRequest.getIsDesc() ? " DESC" : " ASC")
                : "id DESC");

        Page<FinancialDetail> page = lambdaQuery()
                .between(Objects.nonNull(pageRequest.getStartDate()) && Objects.nonNull(pageRequest.getEndDate()),
                        FinancialDetail::getCreateTime,
                        pageRequest.getStartDate(),
                        Optional.ofNullable(pageRequest.getEndDate())
                                .map(endDate -> endDate.plusDays(1))
                                .orElse(null))
                .eq(FinancialDetail::getClientId, UserContextUtil.getUser().getUsername())
                .last(StringUtils.isNotBlank(orderBy), orderBy)
                .setEntity(dto.toEntity())
                .page(pageRequest.getPage());

        return PageResponse.build(FinancialDetailVO.fromEntities(page.getRecords())
                .stream()
                .sorted(Comparator.comparing(FinancialDetailVO::getOccurrenceTime)
                        .thenComparing(FinancialDetailVO::getId)
                        .reversed())
                .collect(Collectors.toList()), page.getTotal());
    }


    @Override
    public FinancialDetailVO info(Long id) {
        return FinancialDetailVO.fromEntity(getById(id));
    }

    @Override
    public FinancialDetailVO infoByCurrentUser() {
        return FinancialDetailVO.fromEntity(lambdaQuery()
                .eq(FinancialDetail::getClientId, UserContextUtil.getUserName())
                .orderByDesc(FinancialDetail::getOccurrenceTime)
                .orderByDesc(FinancialDetail::getId)
                .last("limit 1")
                .one());
    }
}
