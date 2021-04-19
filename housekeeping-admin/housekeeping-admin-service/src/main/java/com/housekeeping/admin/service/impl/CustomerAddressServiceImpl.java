package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.mapper.CustomerAddressMapper;
import com.housekeeping.admin.service.IAddressCodingService;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/23 11:35
 */
@Service("customerAddressService")
public class CustomerAddressServiceImpl extends ServiceImpl<CustomerAddressMapper, CustomerAddress> implements ICustomerAddressService {

    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private IAddressCodingService addressCodingService;

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
        customerAddress.setPhonePrefix(customerAddressAddDTO.getPhonePrefix());
        customerAddress.setPhone(customerAddressAddDTO.getPhone());
        customerAddress.setAddress(customerAddressAddDTO.getAddress());
        //把地址存為經緯度
        customerAddress.setLng(customerAddressAddDTO.getLng().toString());
        customerAddress.setLat(customerAddressAddDTO.getLat().toString());
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
            //把地址存為經緯度
            JSONObject jsonObject = (JSONObject) addressCodingService.addressCoding(customerAddress.getAddress()).getData();
            Double lng = new Double(0);
            Double lat = new Double(0);
            try {
                JSONObject result = (JSONObject) jsonObject.get("result");
                JSONObject location = (JSONObject) result.get("location");
                lng = (Double) location.get("lng");
                lat = (Double) location.get("lat");
            }catch (RuntimeException e){
                return R.failed("地址無法識別");
            }
            customerAddress.setLng(lng.toString());
            customerAddress.setLat(lat.toString());
            baseMapper.updateAddress(customerAddressUpdateDTO);
            return R.ok("地址修改成功");
        }else {
            return R.failed("地址已存在");
        }
    }

    @Override
    public R getAddressByUserId(Integer userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);
        Integer customerId = customerDetails.getId();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("customer_id", customerId);
        queryWrapper1.orderByDesc("is_default");
        List<CustomerAddress> customerAddressList = baseMapper.selectList(queryWrapper1);
        return R.ok(customerAddressList, "成功查詢我的地址列表");
    }

    @Transactional
    @Override
    public R setDefault(Integer addressId) {
        /**先查出來*/
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);
        Integer customerId = customerDetails.getId();
        baseMapper.setNotDefault(customerId);
        baseMapper.setDefault(addressId);
        return R.ok("設置成功");
    }

    @Override
    public R getAll(Page page, Integer customerId) {
        QueryWrapper qw = new QueryWrapper();
        if (CommonUtils.isNotEmpty(customerId)){
            qw.eq("customer_id", customerId);
        }
        Page customerAddressList = baseMapper.selectPage(page, qw);
        return R.ok(customerAddressList, "分頁查詢成功");
    }

}
