package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.HoldBuyerMapper;
import com.gba.client.service.HoldBuyerService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.HoldBuyerDTO;
import com.gba.common.model.entity.HoldBuyer;
import com.gba.common.model.vo.HoldBuyerVO;
import com.gba.common.util.StockTypeUtil;
import com.gba.common.util.StockUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 买方持仓 服务实现类
 * </p>
 *
 * @author lxd
 * @since 2024-03-06 10:23:19
 */
@Service
public class HoldBuyerServiceImpl extends ServiceImpl<HoldBuyerMapper, HoldBuyer> implements HoldBuyerService {
 @Override
    public PageResponse<HoldBuyerVO> page(PageRequest<HoldBuyerDTO, HoldBuyer> pageRequest) {
       HoldBuyerDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new HoldBuyerDTO());

       //动态排序 默认根据id倒序
       String orderBy = "ORDER BY " + (StringUtils.isNotBlank(pageRequest.getSort())
               ? pageRequest.getSortToUnderscore()
               + (pageRequest.getIsDesc() != null && pageRequest.getIsDesc() ? " DESC" : " ASC")
               : "id DESC");

        Page<HoldBuyer> page = lambdaQuery()
                .between(pageRequest.getQueryTimeType() != null
                                && Objects.nonNull(pageRequest.getStartDate())
                                && Objects.nonNull(pageRequest.getEndDate()),
                        HoldBuyer.TimeType.fromCode(Optional.ofNullable(pageRequest.getQueryTimeType()).orElse(0)).getTimeFunction(),
                        pageRequest.getStartDate(),
                        Optional.ofNullable(pageRequest.getEndDate())
                                .map(endDate -> endDate.plusDays(1))
                                .orElse(null))
                .eq(HoldBuyer::getBuyerId, UserContextUtil.getUser().getUsername())
                .ne(HoldBuyer::getClosed, HoldBuyer.ClosedType.CLOSED.getCode())
                .setEntity(dto.toEntity())
                .last(StringUtils.isNotBlank(orderBy), orderBy)
                .page(pageRequest.getPage());

     List<HoldBuyerVO> vos = HoldBuyerVO.fromEntities(page.getRecords());
     if (page.getTotal() > 0) {
         List<String> stockCodes = vos.stream().map(HoldBuyerVO::getStockCode).distinct().collect(Collectors.toList());
         Map<String, BigDecimal> prickMap = StockUtil.getPricesByTencent(stockCodes);
         vos.forEach(vo -> {
             vo.setCurrencyUnit(StockTypeUtil.getTypeByStock(vo.getStockCode()).name());
             vo.setMarketPrice(prickMap.getOrDefault(vo.getStockCode(), BigDecimal.ZERO));
             BigDecimal diff = vo.getMarketPrice().subtract(vo.getExecutionPrice());
             //市价 > 执行价
             if (diff.compareTo(BigDecimal.ZERO) > 0) {
                 //差值 * 合约分数
                 vo.setEstimatedRevenue(diff.multiply(new BigDecimal(vo.getContractNum())).setScale(2, RoundingMode.HALF_UP));
             } else {
                 vo.setEstimatedRevenue(BigDecimal.ZERO);
             }
         });
     }

     return PageResponse.build(vos, page.getTotal());
    }
}
