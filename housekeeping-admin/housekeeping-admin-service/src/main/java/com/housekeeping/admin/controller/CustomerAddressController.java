package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.CustomerAddressAddDTO;
import com.housekeeping.admin.dto.CustomerAddressUpdateDTO;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
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

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】添加地址")
    @PostMapping
    public R addAddress(@RequestBody CustomerAddressAddDTO customerAddressAddDTO){
        return customerAddressService.addAddress(customerAddressAddDTO);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】修改地址")
    @PutMapping
    public R updateAddress(@RequestBody CustomerAddressUpdateDTO customerAddressUpdateDTO){
        return customerAddressService.updateAddress(customerAddressUpdateDTO);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】查询我的地址列表")
    @GetMapping("/mine")
    public R getAddressByUserId(){
        Integer userId = TokenUtils.getCurrentUserId();
        return customerAddressService.getAddressByUserId(userId);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客戶】設置為默認地址")
    @GetMapping("/setDefault")
    public R setDefault(Integer addressId){
        return customerAddressService.setDefault(addressId);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/page")
    @ApiOperation("【管理員】分页查询用戶地址")
    public R getAll(Page page, Integer customerId){
        return customerAddressService.getAll(page, customerId);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】刪除地址")
    @DeleteMapping("/{id}")
    public R deleteAddress(@PathVariable Integer id){
        customerAddressService.removeById(id);
        return R.ok("删除成功");
    }

}
