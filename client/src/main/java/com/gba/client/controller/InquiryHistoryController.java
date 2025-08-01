package com.gba.client.controller;

import com.gba.client.service.InquiryHistoryService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.InquiryHistoryDTO;
import com.gba.common.model.entity.InquiryHistory;
import com.gba.common.model.vo.InquiryHistoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 询价历史Controller
 *
 * @author lxd
 * @since 2024-03-20 04:45:46
 */
@Tag(name = "询价历史")
@RestController
@RequestMapping("inquiry-history")
public class InquiryHistoryController {
    @Autowired
    private InquiryHistoryService service;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public PageResponse<InquiryHistoryVO> pageByLevel1(@RequestBody @Valid PageRequest<InquiryHistoryDTO, InquiryHistory> pageRequest) {
        return service.pageByLevel1(pageRequest);
    }
}
