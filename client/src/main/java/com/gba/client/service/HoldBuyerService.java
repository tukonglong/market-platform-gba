package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.HoldBuyerDTO;
import com.gba.common.model.entity.HoldBuyer;
import com.gba.common.model.vo.HoldBuyerVO;

/**
 * <p>
 * 买方持仓Service
 * </p>
 *
 * @author lxd
 * @since 2024-03-06 10:23:19
 */
public interface HoldBuyerService extends IService<HoldBuyer> {
   /**
    * 分页
    *
    * @param pageRequest
    * @return
    */
   PageResponse<HoldBuyerVO> page(PageRequest<HoldBuyerDTO, HoldBuyer> pageRequest);
}
