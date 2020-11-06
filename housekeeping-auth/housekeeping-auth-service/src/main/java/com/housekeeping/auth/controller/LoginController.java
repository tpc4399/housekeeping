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

    private final ILoginService loginService;

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
