package com.housekeeping.auth.controller;

import com.housekeeping.auth.service.IHkUserService;
import com.housekeeping.common.entity.HkUser;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author su
 * @create 2020/10/28 16:13
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping("/hkUser")
public class HkUserController {

    private final IHkUserService hkUserService;

    @GetMapping("/byEmail")
    public Object byEmail(@RequestParam("email") String email,
                          @RequestParam("deptId") Integer deptId){
        return hkUserService.byEmail(email, deptId);
    }

    @GetMapping("/byPhone")
    public Object byPhone(@RequestParam("phonePrefix") String phonePrefix,
                          @RequestParam("phone") String phone,
                          @RequestParam("deptId") Integer deptId){
        return hkUserService.byPhone(phonePrefix, phone, deptId);
    }
}
