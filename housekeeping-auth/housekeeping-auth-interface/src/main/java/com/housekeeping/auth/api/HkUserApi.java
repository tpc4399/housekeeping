package com.housekeeping.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author su
 * @create 2020/10/28 16:19
 */
@RequestMapping("hkUser")
public interface HkUserApi {

    @GetMapping("byEmail")
    public Object getUserByEmail(@RequestParam String email);


    @GetMapping("byPhone")
    public Object getUserByPhone(@RequestParam String phone);
}
