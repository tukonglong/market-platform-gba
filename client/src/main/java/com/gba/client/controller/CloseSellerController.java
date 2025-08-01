package com.gba.client.controller;

import com.gba.client.service.CloseSellerService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.CloseSellerDTO;
import com.gba.common.model.entity.CloseSeller;
import com.gba.common.model.vo.CloseSellerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 卖方平仓Controller
 *
 * @author lxd
 * @since 2024-03-06 05:07:34
 */
@Tag(name = "卖方平仓")
@RestController
@RequestMapping("close-seller")
public class CloseSellerController {
    @Autowired
    private CloseSellerService service;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public PageResponse<CloseSellerVO> page(@RequestBody @Valid PageRequest<CloseSellerDTO, CloseSeller> pageRequest) {
        return service.page(pageRequest);
    }
}
