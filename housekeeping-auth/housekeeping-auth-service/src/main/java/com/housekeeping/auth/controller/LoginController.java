package com.housekeeping.auth.controller;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 登入注册接口
 * @Author su
 * @create 2020/10/28 16:31
 */
@Api(value="登入注册controller",tags={"登入注册接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final IUserService userService;

    private final ILoginService loginService;

    @ApiOperation("【系统管理员登入】email+password")
    @LogFlag(description = "系统管理员登入【by：email+pwd】")
    @GetMapping("/byEmailPassword1")
    public R loginA1(@RequestParam("email") String email,
                    @RequestParam("password") String password){
        return loginService.loginByEmailAndPasswordHandle(email, password, 1);
    }

    @ApiOperation("【公司人员登入】email+password")
    @LogFlag(description = "公司人员登入【by：email+pwd】")
    @GetMapping("/byEmailPassword2")
    public R loginA2(@RequestParam("email") String email,
                    @RequestParam("password") String password){
        return loginService.loginByEmailAndPasswordHandle(email, password, 2);
    }

    @ApiOperation("【顾客登入】email+password")
    @LogFlag(description = "顾客登入【by：email+pwd】")
    @GetMapping("/byEmailPassword3")
    public R loginA3(@RequestParam("email") String email,
                    @RequestParam("password") String password){
        return loginService.loginByEmailAndPasswordHandle(email, password, 3);
    }

    @ApiOperation("【系统管理员登入】phone+password")
    @LogFlag(description = "系统管理员登入【by：phone+pwd】")
    @GetMapping("/byPhonePassword1")
    public R loginB1(@RequestParam("phone") String phone,
                    @RequestParam("password") String password){
        return loginService.loginByPhoneAndPasswordHandle(phone, password, 1);
    }

    @ApiOperation("【公司人员登入】phone+password")
    @LogFlag(description = "公司人员登入【by：phone+pwd】")
    @GetMapping("/byPhonePassword2")
    public R loginB2(@RequestParam("phone") String phone,
                    @RequestParam("password") String password){
        return loginService.loginByPhoneAndPasswordHandle(phone, password, 2);
    }

    @ApiOperation("【顾客登入】phone+password")
    @LogFlag(description = "顾客登入【by：phone+pwd】")
    @GetMapping("/byPhonePassword3")
    public R loginB3(@RequestParam("phone") String phone,
                    @RequestParam("password") String password){
        return loginService.loginByPhoneAndPasswordHandle(phone, password, 3);
    }

    @ApiOperation("【系统管理员登入】发送验证码")
    @LogFlag(description = "系统管理员手機號登入獲取驗證碼")
    @GetMapping("/SMS1")
    public R loginC1(@RequestParam("phone") String phone){
        return loginService.sendLoginSMSMessage(phone, 1);
    }

    @ApiOperation("【公司人员登入】发送验证码")
    @LogFlag(description = "公司人员手機號登入獲取驗證碼")
    @GetMapping("/SMS2")
    public R loginC2(@RequestParam("phone") String phone){
        return loginService.sendLoginSMSMessage(phone, 2);
    }

    @ApiOperation("【顾客登入】发送验证码")
    @LogFlag(description = "顾客手機號登入獲取驗證碼")
    @GetMapping("/SMS3")
    public R loginC3(@RequestParam("phone") String phone){
        return loginService.sendLoginSMSMessage(phone, 3);
    }

    @ApiOperation("【系统管理员登入】phone+code")
    @LogFlag(description = "系统管理员登入【by：phone+code】")
    @GetMapping("/byPhoneCode1")
    public R loginC1(@RequestParam("phone") String phone,
                    @RequestParam("code") String code){
        return loginService.loginByPhoneAndCodeHandle(phone, code, 1);
    }

    @ApiOperation("【公司人员登入】phone+code")
    @LogFlag(description = "公司人员登入【by：phone+code】")
    @GetMapping("/byPhoneCode2")
    public R loginC2(@RequestParam("phone") String phone,
                    @RequestParam("code") String code){
        return loginService.loginByPhoneAndCodeHandle(phone, code, 2);
    }

    @ApiOperation("【顾客登入】phone+code")
    @LogFlag(description = "顾客登入【by：phone+code】")
    @GetMapping("/byPhoneCode3")
    public R loginC3(@RequestParam("phone") String phone,
                    @RequestParam("code") String code){
        return loginService.loginByPhoneAndCodeHandle(phone, code, 3);
    }

    @ApiOperation("【注册】用户注册")
    @LogFlag(description = "用戶註冊")
    @PostMapping("/registered")
    public R registered(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }

    @ApiOperation("修改密码")
    @LogFlag(description = "修改密碼")
    @GetMapping("/changePw")
    public R changePassword(@RequestParam("newPassword") String newPassword, HttpServletRequest request){
        return loginService.changePw(newPassword, request);
    }

    @ApiOperation("【注销】注销登入")
    @LogFlag(description = "註銷登入")
    @GetMapping("/logout")
    public R logout(HttpServletRequest request){
        return R.ok("注銷成功");
    }
}
