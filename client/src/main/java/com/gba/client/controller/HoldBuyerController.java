package com.gba.client.controller;

import com.gba.client.service.HoldBuyerService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.HoldBuyerDTO;
import com.gba.common.model.entity.HoldBuyer;
import com.gba.common.model.vo.HoldBuyerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 买方持仓Controller
 * </p>
 *
 * @author lxd
 * @since 2024-03-06 10:23:19
 */
@Tag(name = "买方持仓")
@RestController
@RequestMapping("hold-buyer")
public class HoldBuyerController {
    @Autowired
    private HoldBuyerService service;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public PageResponse<HoldBuyerVO> page(@RequestBody @Valid PageRequest<HoldBuyerDTO, HoldBuyer> pageRequest) {
        return service.page(pageRequest);
    }
}
