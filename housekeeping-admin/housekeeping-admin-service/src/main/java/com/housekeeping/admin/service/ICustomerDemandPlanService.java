package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CustomerDemandPlan;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2020/12/28 14:26
 */
public interface ICustomerDemandPlanService extends IService<CustomerDemandPlan> {

    R setJobContends(Integer id, Integer jobsId);

}
