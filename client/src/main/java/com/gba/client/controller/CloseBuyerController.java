package com.gba.client.controller;

import com.gba.client.service.CloseBuyerService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.CloseBuyerDTO;
import com.gba.common.model.entity.CloseBuyer;
import com.gba.common.model.vo.CloseBuyerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 买方平仓表Controller
 *
 * @author lxd
 * @since 2024-03-06 01:29:06
 */
@Tag(name = "买方平仓表")
@RestController
@RequestMapping("close-buyer")
public class CloseBuyerController {
    @Autowired
    private CloseBuyerService service;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public PageResponse<CloseBuyerVO> page(@RequestBody @Valid PageRequest<CloseBuyerDTO, CloseBuyer> pageRequest) {
        return service.page(pageRequest);
    }
}
