package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.mapper.CustomerAddressMapper;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author su
 * @create 2020/11/23 11:35
 */
@Service("customerAddressService")
public class CustomerAddressServiceImpl extends ServiceImpl<CustomerAddressMapper, CustomerAddress> implements ICustomerAddressService {

    @Resource
    private ICustomerDetailsService customerDetailsService;


    @Override
    public R addAddress(CustomerAddressAddDTO customerAddressAddDTO) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);

        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setCustomerId(customerDetails.getId());
        customerAddress.setIsDefault(false);
        customerAddress.setName(customerAddressAddDTO.getName());
        customerAddress.setAddress(customerAddressAddDTO.getAddress());

        return R.ok(this.save(customerAddress), "添加地址成功");
    }

    @Override
    public R updateAddress(CustomerAddressUpdateDTO customerAddressUpdateDTO) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("id", customerAddressUpdateDTO.getId());
        queryWrapper1.eq("customer_id", customerDetails.getId());
        CustomerAddress customerAddress = this.getOne(queryWrapper1);
        if (CommonUtils.isNotEmpty(customerAddress)){
            baseMapper.updateAddress(customerAddressUpdateDTO);
            return R.ok("地址修改成功");
        }else {
            return R.failed("地址不存在");
        }
    }

}
