package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.CloseSellerDTO;
import com.gba.common.model.entity.CloseSeller;
import com.gba.common.model.vo.CloseSellerVO;

/**
 * 卖方平仓Service
 *
 * @author lxd
 * @since 2024-03-06 05:07:34
 */
public interface CloseSellerService extends IService<CloseSeller> {
   /**
    * 分页
    *
    * @param pageRequest
    * @return
    */
   PageResponse<CloseSellerVO> page(PageRequest<CloseSellerDTO, CloseSeller> pageRequest);
}
