package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.FinancialDetailDTO;
import com.gba.common.model.entity.FinancialDetail;
import com.gba.common.model.vo.FinancialDetailVO;

/**
 * <p>
 * 财务流水Service
 * </p>
 *
 * @author lxd
 * @since 2024-02-29 02:19:01
 */
public interface FinancialDetailService extends IService<FinancialDetail> {
    /**
     * 分页
     *
     * @param pageRequest
     * @return
     */
    PageResponse<FinancialDetailVO> page(PageRequest<FinancialDetailDTO, FinancialDetail> pageRequest);

    /**
     * 明细
     *
     * @param id
     * @return
     */
    FinancialDetailVO info(Long id);

    FinancialDetailVO infoByCurrentUser();
}
