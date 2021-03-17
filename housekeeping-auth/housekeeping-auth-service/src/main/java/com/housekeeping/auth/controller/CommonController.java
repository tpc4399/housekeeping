package com.housekeeping.auth.controller;

import com.housekeeping.admin.entity.User;
import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.auth.service.impl.UserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.*;
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
    private final RedisUtils redisUtils;
    private final IUserService userService;

    @ApiOperation("(修改密码)1校验所输密码是否为原密码")
    @GetMapping("/checkPw")
    public R checkPw(@RequestParam String password){
        return userService.checkPw(password);
    }



    @ApiOperation("(修改密码)2输入新密码")
    @LogFlag(description = "修改密碼")
    @GetMapping("/changePw")
    public R changePassword(@RequestParam("newPassword") String newPassword,
                            @RequestParam("rePassword")String rePassword){
        return loginService.changePw(newPassword,rePassword);
    }

    @ApiOperation("(注销)注销登入")
    @LogFlag(description = "註銷登入")
    @GetMapping("/logout")
    public R logout(HttpServletRequest request){
        return R.ok("注銷成功");
    }

    @ApiOperation("绑定登入邮箱1--发送邮件验证码")
    @GetMapping("/bindingEMail1")
    public R validationEmail1(String email){
        Integer userId = TokenUtils.getCurrentUserId();
        //判断email的存在性
        //得到deptId
        Integer deptId = userService.getDeptIdByUserId(userId);
        //根绝deptId和email获取user
        User user = userService.getOne(deptId, email);
        if (CommonUtils.isNotEmpty(user)){
            return R.failed("該郵箱已經被註冊");
        }
        Map<String, String> map = new HashMap<>();
        String key = CommonConstants.BINDING_EMAIL_PREFIX + userId;
        String code = CommonUtils.getRandomSixCode();
        String value = code + ":" + email;
        redisUtils.set(key, value, CommonConstants.VALID_TIME_MINUTES * 60);//三分钟
        map.put("code", code);
        emailUtils.sendCodeToValidationEmail(email, map, "验证邮箱");
        return R.ok("成功發送驗證碼");
    }

    @ApiOperation("绑定登入邮箱2--提交")
    @GetMapping("/bindingEMail2")
    public R validationEmail2(String code){
        Integer userId = TokenUtils.getCurrentUserId();
        String key = CommonConstants.BINDING_EMAIL_PREFIX + userId;
        String value = (String) redisUtils.get(key);
        if (value == null){
            return R.failed("驗證失敗，代碼過期");
        }else {
            String[] values = value.split(":");
            if (code.equals(values[0])){
                //驗證通過
                String email = values[1];
                userService.bindingEmailByUserId(userId, email);
                return R.ok("綁定成功！");
            }else {
                return R.failed("驗證失敗，代碼錯誤");
            }
        }
    }

    @GetMapping("/sendSms")
    @ApiOperation("(更换绑定手机号)1发送手机验证码")
    public R sendSms(){
        return userService.sendSms();
    }

    @GetMapping("/checkCode")
    @ApiOperation("(更换绑定手机号)2验证手机验证码")
    public R checkCode(@RequestParam("code")String code){
        return userService.checkCode(code);
    }

    @GetMapping("/newPhone")
    @ApiOperation("(更换绑定手机号)3发送新手機號驗證碼")
    public R newPhone(@RequestParam("phone")String phone,
                      @RequestParam("phonePrefix")String phonePrefix){
        return userService.newPhone(phone,phonePrefix);
    }

    @GetMapping("/checkCodeByNewPhone")
    @ApiOperation("(更换绑定手机号)4验证新手机号，并修改手机号")
    public R checkCodeByNewPhone(@RequestParam("code")String code,
                                 @RequestParam("phone")String phone,
                                 @RequestParam("phonePrefix")String phonePrefix){
        return userService.checkCodeByNewPhone(code,phone,phonePrefix);
    }

    @GetMapping("/checkToken")
    @ApiOperation("检查token状态")
    public R checkToken(HttpServletRequest request){
        return userService.checkToken(request);
    }
}
