package com.gba.client.bo;

import com.gba.client.service.InquiryHistoryInfoService;
import com.gba.client.service.InquiryHistoryService;
import com.gba.client.service.OfferService;
import com.gba.client.service.OfferThreePartyService;
import com.gba.client.util.UserContextUtil;
import com.gba.common.constant.Constant;
import com.gba.common.model.InquiryRequest;
import com.gba.common.model.InquiryResponse;
import com.gba.common.model.entity.InquiryHistory;
import com.gba.common.model.entity.InquiryHistoryInfo;
import com.gba.common.model.entity.Offer;
import com.gba.common.model.entity.OfferThreeParty;
import com.gba.common.model.enums.StatusEnum;
import com.gba.common.model.enums.TradingTimeEnum;
import com.gba.common.model.vo.OfferThreePartyVO;
import com.gba.common.util.Check;
import com.gba.common.util.ConversionUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in 2024/3/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryBO {
    private final OfferService offerService;
    private final OfferThreePartyService offerThreePartyService;
    private final InquiryHistoryService inquiryHistoryService;
    private final InquiryHistoryInfoService inquiryHistoryInfoService;
    private final HyWebBO hyWebBO;
    private final StockBO stockBO;

    /**
     * 询价
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<InquiryResponse> inquiry(InquiryRequest request) {
        List<String> stockCodes = getValidStockCodes(request);
        Check.checkArgument(CollectionUtils.isNotEmpty(stockCodes), "请选择标的");
        initializeRequestParameters(request);
        List<Double> exercisePricePercents = getExercisePricePercents(request);
        CompletableFuture<List<OfferThreePartyVO>> sellerInquiriesFuture;
        CompletableFuture<List<OfferThreePartyVO>> inquiriesFuture;
        CompletableFuture<List<OfferThreePartyVO>> hyInquiriesFuture = null;

        // 根据交易类型选择不同的报价获取方式
        switch (request.getTradingType()) {
            case 1:
                sellerInquiriesFuture = getSellerInquiriesFuture(stockCodes, request, exercisePricePercents, TradingTimeEnum.T1);
                inquiriesFuture = getInquiriesFuture(stockCodes, request, exercisePricePercents, TradingTimeEnum.T1);
                break;

            case 5:
                sellerInquiriesFuture = getSellerInquiriesFuture(stockCodes, request, exercisePricePercents, TradingTimeEnum.T5);
                inquiriesFuture = getInquiriesFuture(stockCodes, request, exercisePricePercents, TradingTimeEnum.T5);
                hyInquiriesFuture = CompletableFuture.supplyAsync(() -> hyWebBO.inquiry(request));
                break;

            default:
                hyInquiriesFuture = CompletableFuture.supplyAsync(() -> hyWebBO.inquiry(request));
                sellerInquiriesFuture = getSellerInquiriesFuture(stockCodes, request, exercisePricePercents, null);
                inquiriesFuture = getInquiriesFuture(stockCodes, request, exercisePricePercents, null);
                break;
        }

        // 获取异步结果
        List<OfferThreePartyVO> sellerInquiries = getFutureResult(sellerInquiriesFuture);
        List<OfferThreePartyVO> inquiries = getFutureResult(inquiriesFuture);
        List<OfferThreePartyVO> hyInquiries = hyInquiriesFuture != null ? getFutureResult(hyInquiriesFuture) : Lists.newArrayList();

        // 汇总并处理报价
        List<OfferThreePartyVO> allOffers = new ArrayList<>();
        allOffers.addAll(sellerInquiries);
        allOffers.addAll(hyInquiries);
        allOffers.addAll(inquiries);

        List<OfferThreePartyVO> lowestOffers = getLowestOfferSellers(allOffers);
        List<OfferThreePartyVO> finalOffers = new ArrayList<>(lowestOffers);
        //加价
        finalOffers.forEach(offer -> offer.setOffer(new BigDecimal(offer.getOffer().replace("%", "")).add(UserContextUtil.getUser().getIncrease()) + "%"));
        // 构建历史记录
        Map<String, String> stockMap = stockBO.getStockInfoMap();
        List<InquiryHistoryInfo> historyInfos = createInquiryHistory(finalOffers, stockMap, request, exercisePricePercents);
        inquiryHistoryInfoService.saveBatch(historyInfos.stream().distinct().collect(Collectors.toList()));

        // 创建响应
        List<InquiryResponse> inquiryResponses = createInquiryResponses(finalOffers, stockMap, request);
        Check.checkArgument(CollectionUtils.isNotEmpty(inquiryResponses), "暂无报价");
        return inquiryResponses;
    }

    // 1. 初始化请求参数
    private void initializeRequestParameters(InquiryRequest request) {
        if (request.getScale() == null || request.getScale() == 0) {
            request.setScale(1000000);
        }
        if (CollectionUtils.isEmpty(request.getTimeLimitType())) {
            request.setTimeLimitType(Lists.newArrayList("1M"));
        }
        if (CollectionUtils.isEmpty(request.getStrategyTypes())) {
            InquiryRequest.StrategyType strategyType = new InquiryRequest.StrategyType();
            strategyType.setParticipationRate(1).setExercisePricePercent(1d);
            request.setStrategyTypes(Lists.newArrayList(strategyType));
        }
        processExercisePricePercent(request);
    }


    // 2. 处理百分比逻辑
    private void processExercisePricePercent(InquiryRequest request) {
        request.getStrategyTypes().forEach(strategyType -> {
            if (StringUtils.isNotBlank(strategyType.getExercisePricePercentStr())) {
                strategyType.setExercisePricePercent(Double.parseDouble(strategyType.getExercisePricePercentStr().replace("C", "")) / 100);
            }
        });
    }

    // 3. 获取合法的股票代码
    private List<String> getValidStockCodes(InquiryRequest request) {
        return request.getStocks().stream()
                .map(InquiryRequest.Stock::getStockNo)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    // 4. 获取执行价格百分比
    private List<Double> getExercisePricePercents(InquiryRequest request) {
        List<Double> exercisePricePercents = request.getStrategyTypes().stream()
                .map(InquiryRequest.StrategyType::getExercisePricePercent)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(exercisePricePercents)) {
            exercisePricePercents.add(1.0d);
        }
        return exercisePricePercents;
    }

    /**
     * 获取卖家报价异步任务
     */
    private CompletableFuture<List<OfferThreePartyVO>> getSellerInquiriesFuture(List<String> stockCodes, InquiryRequest request, List<Double> exercisePricePercents, TradingTimeEnum tradingTime) {
        return CompletableFuture.supplyAsync(() ->
                OfferThreePartyVO.fromEntities(offerThreePartyService.lambdaQuery()
                        .in(OfferThreeParty::getStockCode, stockCodes)
                        .in(OfferThreeParty::getTimeLimit, request.getTimeLimitType())
                        .in(OfferThreeParty::getExercisePricePercent, exercisePricePercents)
                        .eq(OfferThreeParty::getStatus, StatusEnum.ENABLE.getCode())
                        .eq(tradingTime != null, OfferThreeParty::getTradingTime, tradingTime != null ? tradingTime.getCode() : null)
                        .between(OfferThreeParty::getCreateTime, LocalDate.now(), LocalDate.now().plusDays(1))
                        .list()));
    }

    /**
     * 获取自定义报价异步任务
     */
    private CompletableFuture<List<OfferThreePartyVO>> getInquiriesFuture(List<String> stockCodes, InquiryRequest request, List<Double> exercisePricePercents, TradingTimeEnum tradingTime) {
        return CompletableFuture.supplyAsync(() ->
                OfferThreePartyVO.fromInquiries(offerService.lambdaQuery()
                        .in(Offer::getStockCode, stockCodes)
                        .in(Offer::getTimeLimit, request.getTimeLimitType())
                        .in(Offer::getExercisePricePercent, exercisePricePercents)
                        .eq(tradingTime != null, Offer::getTradingTime, tradingTime != null ? tradingTime.getCode() : null)
                        .between(Offer::getCreateTime, LocalDate.now(), LocalDate.now().plusDays(1))
                        .eq(Offer::getStatus, StatusEnum.ENABLE.getCode())
                        .list()));
    }

    /**
     * 获取异步任务结果
     */
    private <T> List<T> getFutureResult(CompletableFuture<List<T>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取异步任务结果时发生错误: ", e);
            Thread.currentThread().interrupt();
            return Lists.newArrayList();
        }
    }

    /**
     * 根据查询结果创建询价历史记录。
     *
     * @param result                询价卖家信息列表，包含每个卖家的报价详情。
     * @param stockMap              股票代码与股票名称的映射，用于获取股票的名称。
     * @param request               询价请求信息，包含询价的规模等信息。
     * @param exercisePricePercents 行权价格百分比列表，与询价相关但在此方法中未直接使用。
     * @return 返回创建的询价历史信息列表。
     */
    private List<InquiryHistoryInfo> createInquiryHistory(List<OfferThreePartyVO> result, Map<String, String> stockMap, InquiryRequest request, List<Double> exercisePricePercents) {
        List<InquiryHistoryInfo> historyInfos = new ArrayList<>();
        // 根据股票代码对查询结果进行分组，并为每组创建一个询价历史记录
        result.stream()
                .collect(Collectors.groupingBy(OfferThreePartyVO::getStockCode))
                .forEach((key, value) -> {
                    InquiryHistory history = new InquiryHistory();
                    // 设置股票历史记录的基本信息
                    history.setStockName(stockMap.get(key))
                            .setStockCode(key)
                            .setScale(request.getScale());
                    // 保存询价历史记录
                    inquiryHistoryService.save(history);

                    // 为该股票代码下的每个询价创建历史信息
                    value.forEach(inquiry -> historyInfos.add(createInquiryHistoryInfo(inquiry, request, history.getId())));
                    // 补充可能缺失的询价历史信息
                    addMissingInquiryHistoryInfos(historyInfos, request, history.getId(), exercisePricePercents);
                });
        return historyInfos;
    }

    /**
     * 向查询历史信息列表中添加缺失的查询历史信息。
     * 该方法根据提供的股票名称、股票代码、查询历史ID、以及查询请求中的时间限制类型和行权价格百分比，来识别并添加缺失的查询历史记录。
     *
     * @param historyInfos          查询历史信息列表，用于存放新增的缺失信息。
     * @param request               查询请求对象，包含了时间限制类型等信息。
     * @param historyId             查询历史的ID，用于标识新的查询历史记录。
     * @param exercisePricePercents 行权价格百分比列表，用于新增查询历史记录。
     */
    private void addMissingInquiryHistoryInfos(List<InquiryHistoryInfo> historyInfos, InquiryRequest request, Long historyId, List<Double> exercisePricePercents) {
        // 首先，从请求中获取时间限制类型，并移除已经存在于历史信息中的类型
        List<String> timeLimitTypes = Lists.newArrayList(request.getTimeLimitType());

        timeLimitTypes.removeAll(historyInfos.stream().map(InquiryHistoryInfo::getTimeLimit).collect(Collectors.toList()));

        // 遍历剩余的时间限制类型和行权价格百分比，为每种组合创建一个新的查询历史信息并添加到列表中
        timeLimitTypes.forEach(timeLimit -> exercisePricePercents.forEach(exercisePricePercent -> historyInfos.add(
                createMissingInquiryHistoryInfo(historyId, timeLimit, exercisePricePercent))));

        // 然后，从行权价格百分比列表中移除已经存在于历史信息中的百分比
        List<Double> exercisePricePercentsList = Lists.newArrayList(exercisePricePercents);
        exercisePricePercentsList.removeAll(historyInfos.stream().map(info -> info.getExercisePricePercent().doubleValue()).collect(Collectors.toList()));

        // 遍历剩余的行权价格百分比和时间限制类型，为每种组合创建一个新的查询历史信息并添加到列表中
        exercisePricePercentsList.forEach(exercisePricePercent -> timeLimitTypes.forEach(timeLimit -> historyInfos.add(
                createMissingInquiryHistoryInfo(historyId, timeLimit, exercisePricePercent))));
    }

    /**
     * 创建一个缺失查询历史信息的对象。
     * 该方法用于构造一个InquiryHistoryInfo实例，初始化其字段值，主要用于表示一条股票查询历史记录。
     *
     * @param historyId            查询历史的唯一标识符，将设置为查询历史信息的历史ID。
     * @param timeLimit            查询的时间限制，将设置为查询历史信息的时间限制。
     * @param exercisePricePercent 行权价格百分比，将转换为BigDecimal类型并设置为查询历史信息的行权价格百分比。
     * @return 返回一个配置了相关字段值的InquiryHistoryInfo对象。
     */
    private InquiryHistoryInfo createMissingInquiryHistoryInfo(Long historyId, String timeLimit, Double exercisePricePercent) {
        // 创建InquiryHistoryInfo实例并设置其属性值
        return new InquiryHistoryInfo()
                .setQuota(0)
                .setSellerName("")
                .setSellerCode("")
                .setHistoryId(historyId)
                .setOffer(BigDecimal.ZERO) // 设置报价为0
                .setExercisePricePercent(BigDecimal.valueOf(exercisePricePercent)) // 设置行权价格百分比
                .setTimeLimit(timeLimit)
                .setTradingTime(0); // 设置交易时间为0
    }

    /**
     * 根据提供的查询结果、库存映射和查询请求创建查询响应列表。
     *
     * @param result   查询结果的列表，包含卖家信息和库存代码。
     * @param stockMap 库存代码与库存数量的映射。
     * @param request  原始的查询请求信息。
     * @return InquiryResponse 查询响应的列表。
     */
    private List<InquiryResponse> createInquiryResponses(List<OfferThreePartyVO> result, Map<String, String> stockMap, InquiryRequest request) {
        // 将查询结果按照库存代码进行分组，然后为每个分组创建查询响应，最后将所有响应收集到一个列表中
        return result.stream()
                .collect(Collectors.groupingBy(OfferThreePartyVO::getStockCode))
                .entrySet()
                .stream()
                .map(entry -> createInquiryResponse(entry.getKey(), entry.getValue(), stockMap, request))
                .collect(Collectors.toList());
    }

    /**
     * 创建查询响应对象。
     *
     * @param stockCode 股票代码
     * @param sellers   销售者列表，包含各个销售者的详细信息
     * @param stockMap  包含股票代码和股票名称映射的Map
     * @param request   查询请求对象，包含查询的规模等信息
     * @return InquiryResponse 查询响应对象，包含股票名称、股票代码、规模信息以及按条件排序的销售者信息列表。
     */
    private InquiryResponse createInquiryResponse(String stockCode, List<OfferThreePartyVO> sellers, Map<String, String> stockMap, InquiryRequest request) {
        // 初始化查询响应对象，并设置股票名称和代码以及规模信息
        InquiryResponse response = new InquiryResponse();
        response.setStockName(stockMap.get(stockCode))
                .setStockCode(stockCode)
                .setScale(request.getScale() / 10000 + "万");

        // 将销售者信息转换为查询响应信息，然后按行使价格百分比和时间限制进行排序
        List<InquiryResponse.Info> infos = sellers.stream()
                .map(info -> createInquiryResponseInfo(info, request))
                .sorted(Comparator.comparing(InquiryResponse.Info::getExercisePricePercent)
                        .thenComparing(InquiryResponse.Info::getTimeLimit))
                .collect(Collectors.toList());
        response.setInfos(infos);

        return response;
    }

    /**
     * 创建一个InquiryResponse.Info对象，并根据传入的InquirySellerVO对象设置其属性值。
     *
     * @param inquiry 一个包含询价信息的InquirySellerVO对象，用于设置InquiryResponse.Info对象的属性。
     * @return 返回一个配置了询价相关属性的InquiryResponse.Info对象。
     */
    private InquiryResponse.Info createInquiryResponseInfo(OfferThreePartyVO inquiry, InquiryRequest request) {
        // 创建InquiryResponse.Info对象并设置属性
        int scale = inquiry.getScale() <= request.getScale() ? inquiry.getScale() : request.getScale();
        return new InquiryResponse.Info()
                .setQuota(scale / 10000 + "万")
                .setOffer(inquiry.getOffer())
                .setExercisePricePercent(inquiry.getExercisePricePercent())
                .setTimeLimit(inquiry.getTimeLimit())
                .setTradingTime(inquiry.getTradingTime())
                .setSellerCode(inquiry.getSellerCode())
                .setSellerName(inquiry.getSellerName());
    }

    /**
     * 创建询价历史信息对象
     *
     * @param inquiry   询价信息，包含卖家名称、卖家代码、报价、行权价格百分比、时间限制和交易时间等
     * @param historyId 历史记录的ID
     * @return InquiryHistoryInfo 询价历史信息对象，填充了卖家信息、历史ID、报价、行权价格百分比、时间限制和交易时间等字段
     */
    private InquiryHistoryInfo createInquiryHistoryInfo(OfferThreePartyVO inquiry, InquiryRequest request, Long historyId) {
        // 创建询价历史信息对象，并设置相关字段值
        InquiryHistoryInfo info = new InquiryHistoryInfo();
        info.setQuota(inquiry.getScale() <= request.getScale() ? inquiry.getScale() : request.getScale())
                .setSellerName(inquiry.getSellerName())
                .setSellerCode(inquiry.getSellerCode())
                .setHistoryId(historyId)
                .setOffer(ConversionUtil.convertStringToBigDecimal(inquiry.getOffer())) // 转换报价为BigDecimal类型
                .setExercisePricePercent(new BigDecimal(inquiry.getExercisePricePercent().replace("C", "")).divide(new BigDecimal(100))) // 移除"C"字符，将行权价格百分比转换为BigDecimal并除以100
                .setTimeLimit(inquiry.getTimeLimit())
                .setTradingTime(Integer.parseInt(inquiry.getTradingTime().replace("T+", ""))) // 移除"T+"，将交易时间转换为Integer类型
                .setStatus(inquiry.getStatus());
        return info;
    }

    /**
     * 获得最低报价卖家
     *
     * @param sellers
     * @return
     */
    public static List<OfferThreePartyVO> getLowestOfferSellers(List<OfferThreePartyVO> sellers) {
        Map<String, OfferThreePartyVO> uniqueSellers = new HashMap<>();

        sellers.forEach(seller -> {
            String key = seller.getStockCode() + "_"
                    + seller.getTimeLimit() + "_"
                    + seller.getExercisePricePercent();

            if (ConversionUtil.convertStringToBigDecimal(seller.getOffer()).compareTo(new BigDecimal(Constant.OFFER)) > 0) {
                if (uniqueSellers.containsKey(key)) {
                    OfferThreePartyVO existingSeller = uniqueSellers.get(key);
                    if (ConversionUtil.convertStringToBigDecimal(existingSeller.getOffer()).compareTo(ConversionUtil.convertStringToBigDecimal(seller.getOffer())) > 0) {
                        uniqueSellers.put(key, seller); // 更新为费率较低的卖方
                    }
                } else {
                    uniqueSellers.put(key, seller);
                }
            }
        });

        return new ArrayList<>(uniqueSellers.values());
    }
}
