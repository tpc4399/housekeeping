package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/23 11:34
 */
public interface ICustomerAddressService extends IService<CustomerAddress> {

    R addAddress(CustomerAddressAddDTO customerAddressAddDTO);

    R updateAddress(CustomerAddressUpdateDTO customerAddressUpdateDTO);

    R getAddressByUserId(Integer userId);

    R setDefault(Integer addressId);

    R getAll(Page page, Integer customerId);

    /* 判断保洁员的默认地址是否存在 */
    Boolean judgeExistenceDefaultCA(Integer employeesId);

    /* 获取保洁员的默认地址 */
    CustomerAddress getDefaultCAByEmployeesId(Integer employeesId);
}
