package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.AdminPageDTO;
import com.housekeeping.admin.dto.ForgetDTO;
import com.housekeeping.admin.dto.RegisterAdminDTO;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="管理員controller",tags={"【管理員】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
@DefaultProperties(defaultFallback = "fallBackMethod")
public class AdminController {

    private final IUserService userService;

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测管理员手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkAdmin/{data}/{type}")
    @HystrixCommand
    public R checkDataAdmin(@PathVariable("data")String data, @PathVariable("type")Integer type) throws InterruptedException {
        R r = this.userService.checkData(1, data, type);
        return r;
    }

    @ApiOperation("註冊1发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/AdminSMS")
    public R registerA(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,1);
    }

    @ApiOperation("管理员註冊2發送资料")
    @LogFlag(description = "注册管理员账号")
    @PostMapping("/saveAdmin")
    public R saveAdmin(@RequestBody RegisterAdminDTO dto){
        return userService.saveAdmin(dto);
    }

    @ApiOperation("管理员忘记密码1发送验证码")
    @LogFlag(description = "平台管理员忘記密碼")
    @GetMapping("/AdminForgetSMS")
    public R adminForgetSMS(@RequestParam("phonePrefix") String phonePrefix,
                            @RequestParam("phone") String phone){
        return userService.sendForgetMSMessage(phonePrefix,phone,1);
    }

    @ApiOperation("管理员忘记密码1发送新密码")
    @LogFlag(description = "管理員忘記密碼,修改密碼")
    @PostMapping("/AdminUpdatePwd")
    public R adminUpdatePwd(@RequestBody ForgetDTO forgetDTO){
        forgetDTO.setDeptId(1);
        return userService.updatePwd(forgetDTO);
    }

    @ApiOperation("管理员校验验证码")
    @GetMapping("/verfifyAdminCode")
    public R verfifyAdminCode(@RequestParam("phonePrefix") String phonePrefix,
                              @RequestParam("phone") String phone,
                              @RequestParam("code")String code){
        return userService.verfifyCode(phonePrefix, phone,code,1);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @GetMapping("/getAllAdmin")
    @ApiOperation("【管理员】查询所有管理员")
    public R getAllAdmin(Page page, AdminPageDTO adminPageDTO){
        return userService.getAllUser(page,adminPageDTO,1);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】获取当前管理员信息")
    @GetMapping("/info")
    public R getCompanyDetailsByUserId(){
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", userId);
        User admin = userService.getOne(queryWrapper);
        if (admin.getDeptId().equals(1)){
            return R.ok(admin);
        } else {
            return R.failed("当前账户不是管理员账户");
        }
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】删除管理员账号")
    @DeleteMapping("/remove")
    public R removeAdmin(@RequestParam("userId")Integer userId){
        return userService.removeAdmin(userId);
    }

    /**
     * 熔断方法
     * 返回值要和被熔断的方法的返回值一致
     * 熔断方法不需要参数
     * @return
     */
    public R fallBackMethod(){
        return R.failed("请求繁忙，请稍后再试！");
    }

}
