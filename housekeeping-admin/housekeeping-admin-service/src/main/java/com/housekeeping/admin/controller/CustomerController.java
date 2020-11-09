package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.ForgetDTO;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="客戶controller",tags={"客戶管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final IUserService userService;

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测客户手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkCus/{data}/{type}")
    public R checkDataCus(@PathVariable("data")String data, @PathVariable("type")Integer type){
        R r = this.userService.checkData(3, data, type);
        return r;
    }

    @ApiOperation("【客户注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/CusSMS")
    public R registerC(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,3);
    }

    @ApiOperation("【注册】客户注册")
    @LogFlag(description = "注册客户账号")
    @PostMapping("/saveCus")
    public R saveCus(@RequestBody RegisterDTO registerDTO){
        return userService.saveCus(registerDTO);
    }


    @ApiOperation("【客户忘記密碼】发送验证码")
    @LogFlag(description = "客户忘記密碼")
    @GetMapping("/CusForgetSMS")
    public R cusForgetSMS(@RequestParam("phonePrefix") String phonePrefix,
                          @RequestParam("phone") String phone){
        return userService.sendForgetMSMessage(phonePrefix,phone,3);
    }

    @ApiOperation("客户忘記密碼,修改密碼")
    @LogFlag(description = "客户忘記密碼,修改密碼")
    @PostMapping("/CusUpdatePwd")
    public R cusUpdatePwd(@RequestBody ForgetDTO forgetDTO){
        forgetDTO.setDeptId(3);
        return userService.updatePwd(forgetDTO);
    }

}
