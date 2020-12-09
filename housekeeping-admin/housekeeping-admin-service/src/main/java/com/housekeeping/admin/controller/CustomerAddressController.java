package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/23 14:12
 */
@Api(value="客戶controller",tags={"【客户地址】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customerAddress")
public class CustomerAddressController {

    private final ICustomerAddressService customerAddressService;

    @ApiOperation("【客户】添加地址")
    @PostMapping
    public R addAddress(@RequestBody CustomerAddressAddDTO customerAddressAddDTO){
        return customerAddressService.addAddress(customerAddressAddDTO);
    }

    @ApiOperation("【客户】修改地址")
    @PutMapping
    public R updateAddress(@RequestBody CustomerAddressUpdateDTO customerAddressUpdateDTO){
        return customerAddressService.updateAddress(customerAddressUpdateDTO);
    }


}
