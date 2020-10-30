package com.housekeeping.auth.controller;

import com.housekeeping.auth.service.IHkUserService;
import com.housekeeping.common.entity.HkUser;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/10/28 16:13
 */
@RestController
@AllArgsConstructor
@RequestMapping("/hkUser")
public class HkUserController {

    private final IHkUserService hkUserService;

    @GetMapping("/byEmail")
    public Object byEmail(String email){
        return hkUserService.byEmail(email);
    }

    @GetMapping("/byPhone")
    public Object byPhone(String phone){
        return hkUserService.byPhone(phone);
    }
}
