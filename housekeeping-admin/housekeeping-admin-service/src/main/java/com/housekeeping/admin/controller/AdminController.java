package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.ForgetDTO;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="管理員controller",tags={"【管理員】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final IUserService userService;

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("【管理员】异步检测管理员手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkAdmin/{data}/{type}")
    public R checkDataAdmin(@PathVariable("data")String data, @PathVariable("type")Integer type){
        R r = this.userService.checkData(1, data, type);
        return r;
    }

    @ApiOperation("【管理员】註冊1发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/AdminSMS")
    public R registerA(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,1);
    }

    @ApiOperation("【管理员】註冊2發送资料")
    @LogFlag(description = "注册管理员账号")
    @PostMapping("/saveAdmin")
    public R saveAdmin(@RequestBody RegisterDTO registerDTO){
        return userService.saveAdmin(registerDTO);
    }

    @ApiOperation("【管理员】忘记密码1发送验证码")
    @LogFlag(description = "平台管理员忘記密碼")
    @GetMapping("/AdminForgetSMS")
    public R adminForgetSMS(@RequestParam("phonePrefix") String phonePrefix,
                            @RequestParam("phone") String phone){
        return userService.sendForgetMSMessage(phonePrefix,phone,1);
    }

    @ApiOperation("【管理员】忘记密码1发送新密码")
    @LogFlag(description = "管理員忘記密碼,修改密碼")
    @PostMapping("/AdminUpdatePwd")
    public R adminUpdatePwd(@RequestBody ForgetDTO forgetDTO){
        forgetDTO.setDeptId(1);
        return userService.updatePwd(forgetDTO);
    }

    @ApiOperation("【管理员】校验验证码")
    @GetMapping("/verfifyAdminCode")
    public R verfifyAdminCode(@RequestParam("phonePrefix") String phonePrefix,
                              @RequestParam("phone") String phone,
                              @RequestParam("code")String code){
        return userService.verfifyCode(phonePrefix, phone,code,1);
    }

}
