package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.entity.CustomerAddress;

/**
 * @Author su
 * @create 2020/11/23 11:33
 */
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {

    void updateAddress(CustomerAddressUpdateDTO customerAddressUpdateDTO);
}
