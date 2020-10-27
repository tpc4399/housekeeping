package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    @GetMapping("/byEmail")
    public R getUserByEmail(String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping("/byPhone")
    public R getUserByPhone(String phone){
        return userService.getUserByPhone(phone);
    }

    @GetMapping("/loginByEmailPassword")
    public R loginA(@RequestParam("email") String email,
                    @RequestParam("password") String password){
        return R.ok("loginByEmailPassword");
    }

    @GetMapping("/loginByPhonePassword")
    public R loginB(@RequestParam("phone") String phone,
                    @RequestParam("password") String password){
        return R.ok("loginByEmailPassword");
    }

    @GetMapping("/loginByPhoneCode1")
    public R loginC(@RequestParam("phone") String phone){
        return R.ok("loginByEmailPassword");
    }
    @GetMapping("/loginByPhoneCode2")
    public R loginC(@RequestParam("phone") String phone,
                    @RequestParam("code") String code){
        return R.ok("loginByEmailPassword");
    }
}
