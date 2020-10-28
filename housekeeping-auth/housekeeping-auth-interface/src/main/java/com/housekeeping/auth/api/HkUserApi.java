package com.housekeeping.auth.api;

import com.housekeeping.auth.entity.HkUser;
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
    public HkUser getUserByEmail(@RequestParam String email);


    @GetMapping("byPhone")
    public HkUser getUserByPhone(@RequestParam String phone);
}
