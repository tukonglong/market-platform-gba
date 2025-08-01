package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.HoldSellerDTO;
import com.gba.common.model.entity.HoldSeller;
import com.gba.common.model.vo.HoldSellerVO;

/**
 * 卖方持仓Service
 *
 * @author lxd
 * @since 2024-03-06 01:01:51
 */
public interface HoldSellerService extends IService<HoldSeller> {
   /**
    * 分页
    *
    * @param pageRequest
    * @return
    */
   PageResponse<HoldSellerVO> page(PageRequest<HoldSellerDTO, HoldSeller> pageRequest);
}
