package com.gba.client.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gba.client.mapper.InquiryMapper;
import com.gba.client.service.OfferService;
import com.gba.common.model.entity.Offer;
import org.springframework.stereotype.Service;

/**
 * 询价 服务实现类
 *
 * @author lxd
 * @since 2024-03-12 10:12:36
 */
@Service
public class OfferServiceImpl extends ServiceImpl<InquiryMapper, Offer> implements OfferService {

}
