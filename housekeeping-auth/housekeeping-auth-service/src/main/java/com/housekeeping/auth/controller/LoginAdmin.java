package com.housekeeping.auth.controller;

import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/4 17:19
 */
@Api(value="系统管理员登入",tags={"系统管理员登入接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/login/admin")
public class LoginAdmin {

    private final ILoginService loginService;

    @ApiOperation("【系统管理员登入】email+password")
    @LogFlag(description = "系统管理员登入【by：email+pwd】")
    @GetMapping("/byEmailPassword1")
    public R loginA1(@RequestParam("email") String email,
                     @RequestParam("password") String password){
        return loginService.loginByEmailAndPasswordHandle(email, password, 1);
    }

    @ApiOperation("【系统管理员登入】phone+password")
    @LogFlag(description = "系统管理员登入【by：phone+pwd】")
    @GetMapping("/byPhonePassword1")
    public R loginB1(@RequestParam("phone") String phone,
                     @RequestParam("password") String password){
        return loginService.loginByPhoneAndPasswordHandle(phone, password, 1);
    }

    @ApiOperation("【系统管理员登入】发送验证码")
    @LogFlag(description = "系统管理员手機號登入獲取驗證碼")
    @GetMapping("/SMS1")
    public R loginC1(@RequestParam("phone") String phone){
        return loginService.sendLoginSMSMessage(phone, 1);
    }

    @ApiOperation("【系统管理员登入】phone+code")
    @LogFlag(description = "系统管理员登入【by：phone+code】")
    @GetMapping("/byPhoneCode1")
    public R loginC1(@RequestParam("phone") String phone,
                     @RequestParam("code") String code){
        return loginService.loginByPhoneAndCodeHandle(phone, code, 1);
    }
}
