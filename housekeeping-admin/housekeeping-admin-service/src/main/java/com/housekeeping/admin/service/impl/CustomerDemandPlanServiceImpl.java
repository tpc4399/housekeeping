package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CustomerDemandPlan;
import com.housekeeping.admin.mapper.CustomerDemandPlanMapper;
import com.housekeeping.admin.service.ICustomerDemandPlanService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2020/12/28 14:29
 */
@Service("customerDemandPlanService")
public class CustomerDemandPlanServiceImpl
        extends ServiceImpl<CustomerDemandPlanMapper, CustomerDemandPlan>
        implements ICustomerDemandPlanService {
}
