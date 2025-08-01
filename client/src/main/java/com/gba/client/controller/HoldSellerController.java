package com.gba.client.controller;

import com.gba.client.service.HoldSellerService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.HoldSellerDTO;
import com.gba.common.model.entity.HoldSeller;
import com.gba.common.model.vo.HoldSellerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 卖方持仓Controller
 *
 * @author lxd
 * @since 2024-03-07 10:48:00
 */
@Tag(name = "卖方持仓")
@RestController
@RequestMapping("hold-seller")
public class HoldSellerController {
    @Autowired
    private HoldSellerService service;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public PageResponse<HoldSellerVO> page(@RequestBody @Valid PageRequest<HoldSellerDTO, HoldSeller> pageRequest) {
        return service.page(pageRequest);
    }
}
