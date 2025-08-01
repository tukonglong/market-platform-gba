package com.gba.client.controller;

import com.gba.client.service.FinancialDetailService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.FinancialDetailDTO;
import com.gba.common.model.entity.FinancialDetail;
import com.gba.common.model.vo.FinancialDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 财务流水Controller
 * </p>
 *
 * @author lxd
 * @since 2024-02-29 02:19:01
 */
@Tag(name = "财务流水")
@RestController
@RequestMapping("financial-detail")
public class FinancialDetailController {
    @Autowired
    private FinancialDetailService service;

    @GetMapping("infoByCurrentUser")
    @Operation(summary = "获取用户账户余额")
    public FinancialDetailVO infoByCurrentUser() {
        return service.infoByCurrentUser();
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public PageResponse<FinancialDetailVO> page(@RequestBody @Valid PageRequest<FinancialDetailDTO, FinancialDetail> pageRequest) {
        return service.page(pageRequest);
    }

    @GetMapping("getType")
    @Operation(summary = "获取业务类型")
    public Map<Integer, String> getType() {
        Map<Integer, String> mappings = new HashMap<>();
        for (FinancialDetail.Type type : FinancialDetail.Type.values()) {
            mappings.put(type.getCode(), type.getValue());
        }

        return mappings;
    }
}
