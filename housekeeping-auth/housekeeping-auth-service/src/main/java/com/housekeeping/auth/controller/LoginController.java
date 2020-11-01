package com.housekeeping.auth.controller;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 登入接口
 * @Author su
 * @create 2020/10/28 16:31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final IUserService userService;

    private final ILoginService loginService;

    @LogFlag(description = "用戶登入【by：email+pwd】")
    @GetMapping("/byEmailPassword")
    public R loginA(@RequestParam("email") String email,
                    @RequestParam("password") String password){
        return loginService.loginByEmailAndPasswordHandle(email, password);
    }

    @GetMapping("/byPhonePassword")
    public R loginB(@RequestParam("phone") String phone,
                    @RequestParam("password") String password){
        return loginService.loginByPhoneAndPasswordHandle(phone, password);
    }

    @GetMapping("/SMS")
    public R loginC(@RequestParam("phone") String phone){
        return loginService.sendLoginSMSMessage(phone);
    }

    @GetMapping("/byPhoneCode")
    public R loginC(@RequestParam("phone") String phone,
                    @RequestParam("code") String code){
        return loginService.loginByPhoneAndCodeHandle(phone, code);
    }

    @PostMapping("/registered")
    public R registered(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }

    @LogFlag(description = "修改密碼")
    @GetMapping("/changePw")
    public R changePassword(@RequestParam("newPassword") String newPassword, HttpServletRequest request){
        return loginService.changePw(newPassword, request);
    }

    @GetMapping("/logout")
    public R logout(HttpServletRequest request){
        return R.ok("注銷成功");
    }
}
