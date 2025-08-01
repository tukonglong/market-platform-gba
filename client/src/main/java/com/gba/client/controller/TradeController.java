package com.gba.client.controller;

import com.gba.common.config.CustomException;
import com.gba.common.support.anno.Idempotent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxd
 * @since 2024-02-21 04:39:03
 */
@Tag(name = "交易")
@RestController
@RequestMapping("trade")
public class TradeController {

    @Idempotent
    @GetMapping("/order")
    @Operation(summary = "下单")
    public void order() {
        throw new CustomException("功能暂未开放");
        //TODO 企业微信机器人
    }


    @GetMapping("/close")
    @Operation(summary = "平仓")
    public void close() {
        throw new CustomException("功能暂未开放");
        //TODO 企业微信机器人
    }
}
