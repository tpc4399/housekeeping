package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @create 2020/11/23 10:55
 */
public interface ICustomerDetailsService extends IService<CustomerDetails> {

    R updateAddress(Integer id);
}
