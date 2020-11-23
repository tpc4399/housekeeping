package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.mapper.CustomerDetailsMapper;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2020/11/23 10:56
 */
@Service("customerDetailsService")
public class CustomerDetailsServiceImpl extends ServiceImpl<CustomerDetailsMapper, CustomerDetails> implements ICustomerDetailsService {

    @Resource
    private ICustomerAddressService customerAddressService;

    @Override
    public R updateAddress(Integer id) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = this.getOne(queryWrapper);

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("id", id);
        queryWrapper1.eq("customer_id", customerDetails.getId());
        CustomerAddress customerAddress = customerAddressService.getOne(queryWrapper1);
        if (CommonUtils.isNotEmpty(customerAddress)){
            /** 設置為默認地址 */
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("customer_id", customerDetails.getId());
            List<CustomerAddress> customerAddressList = customerAddressService.list(queryWrapper2);
            List<CustomerAddress> customerAddressList1 = customerAddressList.stream().map(x -> {
                x.setIsDefault(x.getId() == id);
                return x;
            }).collect(Collectors.toList());
            customerAddressService.updateBatchById(customerAddressList1);
            return R.ok("地址修改成功");
        }else {
            return R.failed("地址不存在");
        }
    }
}
