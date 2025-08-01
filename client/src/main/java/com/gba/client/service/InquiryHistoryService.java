package com.gba.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gba.common.model.PageRequest;
import com.gba.common.model.PageResponse;
import com.gba.common.model.dto.InquiryHistoryDTO;
import com.gba.common.model.entity.InquiryHistory;
import com.gba.common.model.vo.InquiryHistoryVO;

/**
 * 询价历史Service
 *
 * @author lxd
 * @since 2024-03-20 04:45:46
 */
public interface InquiryHistoryService extends IService<InquiryHistory> {
   /**
    * 分页
    *
    * @param pageRequest
    * @return
    */
   PageResponse<InquiryHistoryVO> pageByLevel1(PageRequest<InquiryHistoryDTO, InquiryHistory> pageRequest);
}
