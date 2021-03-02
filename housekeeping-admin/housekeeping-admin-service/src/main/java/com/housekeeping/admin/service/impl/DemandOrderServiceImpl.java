package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.mapper.DemandOrderMapper;
import com.housekeeping.admin.service.IDemandOrderService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/3/2 16:41
 */
@Service("demandOrderService")
public class DemandOrderServiceImpl
        extends ServiceImpl<DemandOrderMapper, DemandOrder>
        implements IDemandOrderService {
}
