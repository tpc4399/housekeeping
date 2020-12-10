package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.AdminPageDTO;
import com.housekeeping.admin.dto.ForgetDTO;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="公司人員controller",tags={"【公司】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyHuman")
public class CompanyController {

    private final IUserService userService;
    private final ICompanyDetailsService companyDetailsService;

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("【公司】异步检测公司手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkEmp/{data}/{type}")
    public R checkDataEmp(@PathVariable("data")String data, @PathVariable("type")Integer type){
        R r = this.userService.checkData(2, data, type);
        return r;
    }


    @ApiOperation("【公司】注册1发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/EmpSMS")
    public R registerB(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,2);
    }

    @ApiOperation("【公司】注册2发送注册信息")
    @LogFlag(description = "注册公司账号")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody RegisterDTO registerDTO){
        return userService.saveEmp(registerDTO);
    }

    @ApiOperation("【公司】忘记密码1发送验证码")
    @LogFlag(description = "公司人员忘記密碼")
    @GetMapping("/EmpForgetSMS")
    public R empForgetSMS(@RequestParam("phonePrefix") String phonePrefix,
                          @RequestParam("phone") String phone){
        return userService.sendForgetMSMessage(phonePrefix,phone,2);
    }

    @ApiOperation("【公司】忘记密码2发送新密码")
    @LogFlag(description = "公司忘記密碼,修改密碼")
    @PostMapping("/EmpUpdatePwd")
    public R empUpdatePwd(@RequestBody ForgetDTO forgetDTO){
        forgetDTO.setDeptId(2);
        return userService.updatePwd(forgetDTO);
    }

    @ApiOperation("【公司】校验验证码")
    @GetMapping("/verfifyCompanyCode")
    public R verfifyAdminCode(@RequestParam("phonePrefix") String phonePrefix,
                              @RequestParam("phone") String phone,
                              @RequestParam("code")String code){
        return userService.verfifyCode(phonePrefix, phone,code,2);
    }

    @ApiOperation("【公司】获取公司详情信息")
    @GetMapping("/info")
    public R getCompanyDetailsByUserId(){
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
        if (CommonUtils.isNotEmpty(companyDetails)){
            return R.ok(companyDetails);
        } else {
            return R.failed("公司不存在,请用公司账户获取信息");
        }
    }

    @GetMapping("/getAllCompany")
    @ApiOperation("【管理员】查询所有公司賬戶")
    public R getAllCompany(Page page, AdminPageDTO adminPageDTO){
        return userService.getAllUser(page,adminPageDTO,2);
    }
}