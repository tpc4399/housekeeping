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
@Api(value="公司登陆",tags={"公司登陆接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/login/company")
public class LoginCompany {

    private final ILoginService loginService;

    @ApiOperation("【公司人员登入】email+password")
    @LogFlag(description = "公司人员登入【by：email+pwd】")
    @GetMapping("/byEmailPassword")
    public R loginA(@RequestParam("email") String email,
                     @RequestParam("password") String password){
        return loginService.loginByEmailAndPasswordHandle(email, password, 2);
    }

    @ApiOperation("【公司人员登入】phone+password")
    @LogFlag(description = "公司人员登入【by：phone+pwd】")
    @GetMapping("/byPhonePassword")
    public R loginB(@RequestParam("phonePrefix") String phonePrefix,
                    @RequestParam("phone") String phone,
                     @RequestParam("password") String password){
        return loginService.loginByPhoneAndPasswordHandle(phonePrefix, phone, password, 2);
    }

    @ApiOperation("【公司人员登入】发送验证码")
    @LogFlag(description = "公司人员手機號登入獲取驗證碼")
    @GetMapping("/SMS")
    public R loginC(@RequestParam("phonePrefix") String phonePrefix,
                    @RequestParam("phone") String phone){
        return loginService.sendLoginSMSMessage(phonePrefix, phone, 2);
    }

    @ApiOperation("【公司人员登入】phone+code")
    @LogFlag(description = "公司人员登入【by：phone+code】")
    @GetMapping("/byPhoneCode")
    public R loginC(@RequestParam("phonePrefix") String phonePrefix,
                    @RequestParam("phone") String phone,
                     @RequestParam("code") String code){
        return loginService.loginByPhoneAndCodeHandle(phonePrefix, phone, code, 2);
    }
}
