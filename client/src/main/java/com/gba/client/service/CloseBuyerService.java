package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.CloseBuyerDTO;
import com.gba.common.model.entity.CloseBuyer;
import com.gba.common.model.vo.CloseBuyerVO;

/**
 * 买方平仓表Service
 *
 * @author lxd
 * @since 2024-03-06 01:29:06
 */
public interface CloseBuyerService extends IService<CloseBuyer> {
   /**
    * 分页
    *
    * @param pageRequest
    * @return
    */
   PageResponse<CloseBuyerVO> page(PageRequest<CloseBuyerDTO, CloseBuyer> pageRequest);
}
