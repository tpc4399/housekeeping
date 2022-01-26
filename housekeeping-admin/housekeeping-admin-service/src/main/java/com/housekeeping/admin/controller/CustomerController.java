package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="客戶controller",tags={"【客戶】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final IUserService userService;
    private final ICustomerDetailsService customerDetailsService;

    /**
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测客户手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkCus/{data}/{type}")
    public R checkDataCus(@PathVariable("data")String data, @PathVariable("type")String type){
        R r = this.userService.checkData(data, type,3);
        return r;
    }

    @ApiOperation("注册1发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/CusSMS")
    public R registerC(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone) throws Exception {
        return userService.sendRegisterMSMessage(phonePrefix,phone,3);
    }

    @ApiOperation("客户注册2发送注册信息")
    @LogFlag(description = "注册客户账号")
    @PostMapping("/saveCus")
    public R saveCus(@RequestBody RegisterCustomerDTO dto){
        return userService.saveCus(dto);
    }

    @ApiOperation("【管理员】获取客戶详情信息")
    @GetMapping("/GetInfoById")
    public R GetInfoById(@RequestParam("userId")Integer userId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);
        if (CommonUtils.isNotEmpty(customerDetails)){
            return R.ok(customerDetails);
        } else {
            return R.failed("该客戶不存在");
        }
    }

    @ApiOperation("【客戶】获取客戶详情信息")
    @GetMapping("/info")
    public R getCustomerDetailsByUserId(){
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);
        if (CommonUtils.isNotEmpty(customerDetails)){
            return R.ok(customerDetails);
        } else {
            return R.failed("客戶不存在,请用客戶账户获取信息");
        }
    }

    @ApiOperation("【客戶】修改客户信息")
    @PutMapping("/updateCus")
    public R updateCus(@RequestBody CustomerUpdateDTO customerUpdateDTO){
        return customerDetailsService.updateCus(customerUpdateDTO);
    }

    @GetMapping("/getAllCustomer")
    @ApiOperation("【管理员】查询所有客戶")
    public R getAllAdmin(Page page, AdminPageDTO adminPageDTO){
        return userService.getAllUser(page,adminPageDTO,3);
    }

    @ApiOperation("【管理员】删除客户")
    @DeleteMapping("/removeCus")
    public R removeCus(@RequestParam("userId")Integer userId){
        return userService.removeCus(userId);
    }


}
