package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.DemandOrderDetails;
import com.housekeeping.admin.mapper.DemandOrderDetailsMapper;
import com.housekeeping.admin.service.IDemandOrderDetailsService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/3/2 16:41
 */
@Service("demandOrderDetailsService")
public class DemandOrderDetailsServiceImpl
        extends ServiceImpl<DemandOrderDetailsMapper, DemandOrderDetails>
        implements IDemandOrderDetailsService {
}
