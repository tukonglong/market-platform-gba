package com.gba.client.controller;

import com.gba.client.bo.InquiryBO;
import com.gba.client.bo.StockBO;
import com.gba.common.model.InquiryRequest;
import com.gba.common.model.InquiryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 询价Controller
 *
 * @author lxd
 * @since 2024-03-12 10:12:36
 */
@Tag(name = "询价")
@RestController
@RequestMapping("inquiry")
public class InquiryController {
    @Autowired
    InquiryBO inquiryBO;

    @Autowired
    StockBO stockBO;

    @PostMapping("/getInquiry")
    @Operation(summary = "询价")
    public List<InquiryResponse> getInquiry(@RequestBody InquiryRequest request) {
        return inquiryBO.inquiry(request);
    }

    @GetMapping("/getStockInfo")
    @Operation(summary = "获取标的明细")
    public List<String> getStockInfo() {
        return stockBO.getStockInfoList();
    }
}
