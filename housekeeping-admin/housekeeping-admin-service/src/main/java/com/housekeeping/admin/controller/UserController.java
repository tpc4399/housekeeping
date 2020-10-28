package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    @GetMapping("/byEmail")
    public R getUserByEmail(@RequestParam String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping("/byPhone")
    public R getUserByPhone(@RequestParam String phone){
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

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public R checkDataUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean b = this.userService.checkData(data, type);
        if(b == null){
            return R.failed("请求参数错误");
        }
        return R.ok(b);
    }

}
