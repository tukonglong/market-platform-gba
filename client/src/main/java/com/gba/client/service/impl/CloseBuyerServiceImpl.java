package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.CloseBuyerMapper;
import com.gba.client.service.CloseBuyerService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.CloseBuyerDTO;
import com.gba.common.model.entity.CloseBuyer;
import com.gba.common.model.vo.CloseBuyerVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * 买方平仓表 服务实现类
 *
 * @author lxd
 * @since 2024-03-06 01:29:06
 */
@Service
public class CloseBuyerServiceImpl extends ServiceImpl<CloseBuyerMapper, CloseBuyer> implements CloseBuyerService {
    @Override
    public PageResponse<CloseBuyerVO> page(PageRequest<CloseBuyerDTO, CloseBuyer> pageRequest) {
        CloseBuyerDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new CloseBuyerDTO());

        //动态排序 默认根据id倒序
        String orderBy = "ORDER BY " + (StringUtils.isNotBlank(pageRequest.getSort())
                ? pageRequest.getSortToUnderscore()
                + (pageRequest.getIsDesc() != null && pageRequest.getIsDesc() ? " DESC" : " ASC")
                : "id DESC");

        Page<CloseBuyer> page = lambdaQuery()
                .between(pageRequest.getQueryTimeType() != null
                                && Objects.nonNull(pageRequest.getStartDate())
                                && Objects.nonNull(pageRequest.getEndDate()),
                        CloseBuyer.TimeType.fromCode(Optional.ofNullable(pageRequest.getQueryTimeType()).orElse(0)).getTimeFunction(),
                        pageRequest.getStartDate(),
                        Optional.ofNullable(pageRequest.getEndDate())
                                .map(endDate -> endDate.plusDays(1))
                                .orElse(null))
                .eq(CloseBuyer::getBuyerId, UserContextUtil.getUser().getUsername())
                .last(StringUtils.isNotBlank(orderBy), orderBy)
                .setEntity(dto.toEntity())
                .page(pageRequest.getPage());

        return PageResponse.build(CloseBuyerVO.fromEntities(page.getRecords()), page.getTotal());
    }
}
