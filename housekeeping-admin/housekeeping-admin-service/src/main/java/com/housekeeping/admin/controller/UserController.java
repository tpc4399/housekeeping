package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
