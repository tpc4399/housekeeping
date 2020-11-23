package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/23 10:55
 */
public interface ICustomerDetailsService extends IService<CustomerDetails> {

    R toDefault(Integer id);
}
