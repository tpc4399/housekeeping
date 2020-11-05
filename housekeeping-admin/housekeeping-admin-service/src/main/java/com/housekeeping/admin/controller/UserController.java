package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="用户controller",tags={"用户管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;


    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测管理员手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkAdmin/{data}/{type}")
    public R checkDataAdmin(@PathVariable("data")String data,@PathVariable("type")Integer type){
        R r = this.userService.checkData(1, data, type);
        return r;
    }

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测公司手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkEmp/{data}/{type}")
    public R checkDataEmp(@PathVariable("data")String data,@PathVariable("type")Integer type){
        R r = this.userService.checkData(2, data, type);
        return r;
    }

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测客户手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkCus/{data}/{type}")
    public R checkDataCus(@PathVariable("data")String data,@PathVariable("type")Integer type){
        R r = this.userService.checkData(3, data, type);
        return r;
    }

    @ApiOperation("【平台管理员注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/AdminSMS")
    public R registerA(@RequestParam("phonePrefix") String phonePrefix,
            @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,1);
    }

    @ApiOperation("【公司人员注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/EmpSMS")
    public R registerB(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,2);
    }

    @ApiOperation("【客户注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/CusSMS")
    public R registerC(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,3);
    }

    @ApiOperation("【注册】公司注册")
    @LogFlag(description = "注册公司账号")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody RegisterDTO registerDTO){
        return userService.saveEmp(registerDTO);
    }

    @ApiOperation("【注册】客户注册")
    @LogFlag(description = "注册客户账号")
    @PostMapping("/saveCus")
    public R saveCus(@RequestBody RegisterDTO registerDTO){
        return userService.saveCus(registerDTO);
    }

    @ApiOperation("【注册】管理员注册")
    @LogFlag(description = "注册管理员账号")
    @PostMapping("/saveAdmin")
    public R saveAdmin(@RequestBody RegisterDTO registerDTO){
        return userService.saveAdmin(registerDTO);
    }
}
