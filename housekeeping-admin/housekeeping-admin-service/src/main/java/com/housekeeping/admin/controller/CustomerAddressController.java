package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
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


    @ApiOperation("【客户】查询我的地址列表")
    @GetMapping("/mine")
    public R getAddressByUserId(){
        Integer userId = TokenUtils.getCurrentUserId();
        return customerAddressService.getAddressByUserId(userId);
    }

    @ApiOperation("【客戶】設置為默認地址")
    @GetMapping("/setDefault")
    public R setDefault(Integer addressId){
        return customerAddressService.setDefault(addressId);
    }

    @ApiOperation("【管理員】查詢所有用戶地址")
    public R getAll(Page page){
        return customerAddressService.getAll(page);
    }

    @ApiOperation("刪除地址")
    @DeleteMapping("/{id}")
    public R deleteAddress(@PathVariable Integer id){
        customerAddressService.removeById(id);
        return R.ok("删除成功");
    }

}
