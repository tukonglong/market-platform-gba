package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.HoldSellerMapper;
import com.gba.client.service.HoldSellerService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.HoldSellerDTO;
import com.gba.common.model.entity.HoldBuyer;
import com.gba.common.model.entity.HoldSeller;
import com.gba.common.model.vo.HoldSellerVO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 卖方持仓 服务实现类
 *
 * @author lxd
 * @since 2024-03-06 01:01:51
 */
@Service
public class HoldSellerServiceImpl extends ServiceImpl<HoldSellerMapper, HoldSeller> implements HoldSellerService {
    @Override
    public PageResponse<HoldSellerVO> page(PageRequest<HoldSellerDTO, HoldSeller> pageRequest) {
        HoldSellerDTO dto = Optional.ofNullable(pageRequest.getCondition()).orElse(new HoldSellerDTO());

        //动态排序 默认根据id倒序
        String orderBy = "ORDER BY " + (StringUtils.isNotBlank(pageRequest.getSort())
                ? pageRequest.getSortToUnderscore()
                + (pageRequest.getIsDesc() != null && pageRequest.getIsDesc() ? " DESC" : " ASC")
                : "id DESC");

        Page<HoldSeller> page = lambdaQuery()
                .between(pageRequest.getQueryTimeType() != null
                                && Objects.nonNull(pageRequest.getStartDate())
                                && Objects.nonNull(pageRequest.getEndDate()),
                        HoldSeller.TimeType.fromCode(Optional.ofNullable(pageRequest.getQueryTimeType()).orElse(0)).getTimeFunction(),
                        pageRequest.getStartDate(),
                        Optional.ofNullable(pageRequest.getEndDate())
                                .map(endDate -> endDate.plusDays(1))
                                .orElse(null))
                .eq(HoldSeller::getSellerId, UserContextUtil.getUser().getUsername())
                .ne(HoldSeller::getClosed, HoldBuyer.ClosedType.CLOSED.getCode())
                .setEntity(dto.toEntity())
                .last(StringUtils.isNotBlank(orderBy), orderBy)
                .page(pageRequest.getPage());
        Map<String, HoldSeller> holdMap = page.getRecords().stream().collect(Collectors.toMap(HoldSeller::getSellerOrderId, Function.identity(), (k1, k2) -> k1));
        List<HoldSellerVO> vos = HoldSellerVO.fromEntities(page.getRecords());
        if (page.getTotal() > 0) {
            List<String> stockCodes = vos.stream().map(HoldSellerVO::getStockCode).distinct().collect(Collectors.toList());
            Map<String, BigDecimal> prickMap = StockUtil.getPricesByTencent(stockCodes);
            vos.forEach(vo -> {
                BigDecimal marketPrice = prickMap.get(vo.getStockCode());
                vo.setCurrencyUnit(StockTypeUtil.getTypeByStock(vo.getStockCode()).name());
                vo.setMarketPrice(marketPrice);

                HoldSeller hold = holdMap.get(vo.getSellerOrderId());
                //浮动盈亏 (执行价 - 市价) * 合约份数 > 期权费 ? 期权费 : 持仓盈亏
                BigDecimal position = hold.getExecutionPrice().subtract(marketPrice).multiply(new BigDecimal(hold.getContractNum()));
                if (position.compareTo(hold.getOptionPremium()) > 0) {
                    position = hold.getOptionPremium();
                }
                vo.setPosition(position);

                //风险度 = -持仓盈亏 / 名义本金/(实际本金)
                vo.setRiskDegree(position.negate()
                        .divide(BigDecimal.valueOf(hold.getAmount()), 6, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100))
                        .setScale(2, RoundingMode.HALF_UP) + "%");
            });
        }

        return PageResponse.build(vos, page.getTotal());
    }
}
