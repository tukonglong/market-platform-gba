package com.gba.client.bo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gba.common.constant.HYConstant;
import com.gba.common.model.InquiryRequest;
import com.gba.common.model.hy.HYWebInquiryResponse;
import com.gba.common.model.vo.OfferThreePartyVO;
import com.gba.common.util.OKHttpUtil;
import com.gba.common.util.RedisDataBase2Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liuxudong
 * @Description: 华裕询价
 * @Date: Created in 2024/3/7
 */
@Service
@Slf4j
public class HyWebBO {
    @Autowired
    private RedisDataBase2Utils redisDataBase2Utils;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public String loginByWeb() {
        Map<String, Object> param = createLoginParams();
        Map<String, String> headers = createHeaders();
        try (Response response = OKHttpUtil.doUnsafePost(HYConstant.WEB_HOST + HYConstant.LOGIN_URL, param, headers)) {
            if (response == null || response.body() == null || !response.isSuccessful()) {
                log.error("华裕接口登录失败 at {}", LocalDateTime.now());
                return "";
            }

            JsonNode rootNode = OBJECT_MAPPER.readTree(response.body().string());
            log.info("华裕登录接口返参{}", rootNode);

            if (!"10000".equals(rootNode.path("code").asText())) {
                log.error("华裕登录接口异常{}, at {}", rootNode.path("msg").asText(), LocalDateTime.now());
                return "";
            }

            String token = rootNode.path("data").path("accessToken").asText();
            redisDataBase2Utils.setObject(HYConstant.TOKEN_KEY, token, 1800L, TimeUnit.SECONDS);
            return token;
        }
    }

    public List<OfferThreePartyVO> inquiry(InquiryRequest request) {
        List<String> filterList = redisDataBase2Utils.getList(HYConstant.FILTER_KEY);
        if (isRequestFiltered(request, filterList)) {
            return Collections.emptyList();
        }

        addInquiry(request);
        HYWebInquiryResponse model = getInquiry();
        return model != null ? model.toInquiryList() : Collections.emptyList();
    }

    private boolean isRequestFiltered(InquiryRequest request, List<String> filterList) {
        return request != null && CollectionUtils.isNotEmpty(request.getStocks())
                && CollectionUtils.isNotEmpty(filterList)
                && filterList.contains(request.getStocks().get(0).getStockNo());
    }

    /**
     * 询价
     * @param param
     */
    public void addInquiry(InquiryRequest param) {
        String token = (String) Optional.ofNullable(redisDataBase2Utils.getObject(HYConstant.TOKEN_KEY)).orElseGet(this::loginByWeb);
        Map<String, String> headers = createHeaders();
        headers.put("Accesstoken", token);
        headers.put("Referer", "https://option.huayu-holdings.com/transactionOpt/buy");
        param.setInquiryUserId(HYConstant.USER_ID).setStructure("call");

        log.info("华裕询价接口入参{}", param);
        log.info("华裕询价接口请求头{}", headers);
        boolean retry = false;

        do {
            try (Response response = OKHttpUtil.doUnsafePost(HYConstant.WEB_HOST + HYConstant.INQUIRY_ADD_URL, param, headers);
                 ResponseBody responseBody = response.body()) {
                if (responseBody != null) {
                    JsonNode rootNode = OBJECT_MAPPER.readTree(responseBody.string());
                    log.info("华裕询价接口返参{}", rootNode);
                    if ("10000".equals(rootNode.path("code").asText())) {
                        // 正常返回，结束重试
                        retry = false;
                    } else {
                        log.error("华裕询价接口异常: {} at {}", rootNode.path("msg").asText(), LocalDateTime.now());
                        if (!retry) {
                            // 重新获取 token 并重试一次
                            log.info("Token 失效, 重新登录获取新的 token");
                            token = loginByWeb();
                            headers.put("Accesstoken", token);
                            retry = true;
                        } else {
                            // 已经重试过一次，还是失败
                            log.error("华裕询价接口第二次异常, 结束请求");
                            retry = false;
                        }
                    }
                }
            } catch (IOException e) {
                log.error("华裕询价接口发生异常: ", e);
                retry = false;
            }
        } while (retry);
    }

    public HYWebInquiryResponse getInquiry() {
        Map<String, Object> param = createInquiryParams();
        String token = (String) Optional.ofNullable(redisDataBase2Utils.getObject(HYConstant.TOKEN_KEY)).orElseGet(this::loginByWeb);
        Map<String, String> headers = createHeaders();
        headers.put("Accesstoken", token);
        headers.put("Referer", "https://option.huayu-holdings.com/transactionOpt/buy");

        log.info("华裕询价接口入参{}", param);
        log.info("华裕询价接口请求头{}", headers);
        try (Response response = OKHttpUtil.doUnsafePost(HYConstant.WEB_HOST + HYConstant.INQUIRY_PAGE_URL, param, headers)) {
            if (response != null && response.body() != null) {
                String responseBodyString = response.body().string();
                log.info("华裕查询接口返参{}", responseBodyString);
                return JSON.parseObject(responseBodyString, HYWebInquiryResponse.class);
            }
        } catch (IOException e) {
            log.error("华裕查询接口发生异常: ", e);
        }
        return null;
    }

    private Map<String, Object> createLoginParams() {
        Map<String, Object> param = new HashMap<>();
        param.put("loginId", HYConstant.LOGIN_ID);
        param.put("longFlag", "Y");
        param.put("password", HYConstant.PASSWORD);
        param.put("source", "option");
        param.put("Deviceinfo", HYConstant.DEVICE_INFO);
        return param;
    }

    private Map<String, Object> createInquiryParams() {
        Map<String, Object> param = new HashMap<>();
        param.put("pageNum", 1);
        param.put("pageSize", 1);
        Map<String, Object> reqObjMap = new HashMap<>();
        reqObjMap.put("inquiryDateBt", Arrays.asList(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), ""));
        reqObjMap.put("stockNo", "");
        reqObjMap.put("inquiryUserIds", Collections.singletonList(HYConstant.USER_ID));
        param.put("reqObj", reqObjMap);
        return param;
    }

    private Map<String, String> createHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Origin", HYConstant.WEB_HOST);
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36");
        headers.put("Accesssystemid", "option-app");
        headers.put("Cid", "login=" + HYConstant.LOGIN_ID + ",ctrl=" + HYConstant.LOGIN_ID);
        headers.put("Deviceinfo", HYConstant.DEVICE_INFO);
        return headers;
    }
}
