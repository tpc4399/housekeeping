package com.housekeeping.auth.controller;

import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.EmailUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 其他接口
 * @Author su
 * @create 2020/10/28 16:31
 */
@Api(value="其他接口controller",tags={"通用接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/others")
public class CommonController {

    private final ILoginService loginService;
    private final EmailUtils emailUtils;

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

    @ApiOperation("绑定登入邮箱--发送邮件验证码")
    @GetMapping("/bindingEMail")
    public R validationEmail(String email){
        Map<String, String> map = new HashMap<>();
        map.put("code","564535131");
        emailUtils.sendCodeToValidationEmail(email, map, "验证邮箱");
        return R.ok();
    }
}
