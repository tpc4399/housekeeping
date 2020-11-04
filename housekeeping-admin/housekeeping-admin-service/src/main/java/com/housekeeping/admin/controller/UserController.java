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

    @ApiOperation("【查】根据管理员Email")
    @LogFlag(description = "查詢管理员信息【by：email】")
    @GetMapping("/byEmail")
    public User getUserByEmail(@RequestParam String email){
        return userService.getUserByEmail(email,1);
    }

    @ApiOperation("【查】根据管理员phone")
    @LogFlag(description = "查詢管理员信息【by：phone】")
    @GetMapping("/byPhone")
    public User getUserByPhone(@RequestParam String phone){
        User res = userService.getUserByPhone(phone,1);
        return res;
    }

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public R checkDataUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean b = this.userService.checkData(data, type);
        if(b == null){
            return R.failed("请求参数错误");
        }
        return R.ok(b);
    }

    @ApiOperation("【平台管理员注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/SMS")
    public R registerA(@RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phone,1);
    }

    @ApiOperation("【公司人员注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/SMS")
    public R registerB(@RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phone,2);
    }

    @ApiOperation("【客户注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/SMS")
    public R registerC(@RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phone,3);
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
