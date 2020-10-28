package com.housekeeping.auth.controller;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.auth.service.IHkUserService;
import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.auth.service.impl.HkUserService;
import com.housekeeping.auth.service.impl.LoginService;
import com.housekeeping.auth.service.impl.UserService;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 登入接口
 * @Author su
 * @create 2020/10/28 16:31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final IHkUserService hkUserService;

    private final IUserService userService;

    private final ILoginService loginService;

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
        return R.ok("loginByEmailPassword");
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
}
