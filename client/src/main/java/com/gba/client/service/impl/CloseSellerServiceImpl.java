package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.CloseSellerMapper;
import com.gba.client.service.CloseSellerService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.CloseSellerDTO;
import com.gba.common.model.entity.CloseSeller;
import com.gba.common.model.vo.CloseSellerVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * 卖方平仓 服务实现类
 *
 * @author lxd
 * @since 2024-03-06 05:07:34
 */
@Service
public class CloseSellerServiceImpl extends ServiceImpl<CloseSellerMapper, CloseSeller> implements CloseSellerService {
 @Override
    public PageResponse<CloseSellerVO> page(PageRequest<CloseSellerDTO, CloseSeller> pageRequest) {
       CloseSellerDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new CloseSellerDTO());

       //动态排序 默认根据id倒序
       String orderBy = "ORDER BY " + (StringUtils.isNotBlank(pageRequest.getSort())
               ? pageRequest.getSortToUnderscore()
               + (pageRequest.getIsDesc() != null && pageRequest.getIsDesc() ? " DESC" : " ASC")
               : "id DESC");

        Page<CloseSeller> page = lambdaQuery()
                .between(pageRequest.getQueryTimeType() != null
                                && Objects.nonNull(pageRequest.getStartDate())
                                && Objects.nonNull(pageRequest.getEndDate()),
                        CloseSeller.TimeType.fromCode(Optional.ofNullable(pageRequest.getQueryTimeType()).orElse(0)).getTimeFunction(),
                        pageRequest.getStartDate(),
                        Optional.ofNullable(pageRequest.getEndDate())
                                .map(endDate -> endDate.plusDays(1))
                                .orElse(null))
                .eq(CloseSeller::getSellerId, UserContextUtil.getUser().getUsername())
                .last(StringUtils.isNotBlank(orderBy), orderBy)
                .setEntity(dto.toEntity())
                .page(pageRequest.getPage());

        return PageResponse.build(CloseSellerVO.fromEntities(page.getRecords()), page.getTotal());
    }
}
