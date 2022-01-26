package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.AdminPageDTO;
import com.housekeeping.admin.dto.RegisterCompanyDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
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
    @ApiOperation("异步检测公司手机号是否重复(手机号)")
    @GetMapping("/checkEmp/{data}/{type}")
    public R checkDataEmp(@PathVariable("data")String data, @PathVariable("type")String type){
        R r = this.userService.checkData2(data, type);
        return r;
    }


    @ApiOperation("公司注册1发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/EmpSMS")
    public R registerB(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone) throws Exception {
        return userService.sendRegisterMSMessage(phonePrefix,phone,2);
    }

    @ApiOperation("公司注册2发送注册信息")
    @LogFlag(description = "注册公司账号")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody RegisterCompanyDTO dto){
        return userService.saveEmp(dto);
    }


    @Access(RolesEnum.USER_COMPANY)
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

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】获取公司详情信息")
    @GetMapping("/getInfoById")
    public R getInfoById(@RequestParam("userId") Integer userId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
        if (CommonUtils.isNotEmpty(companyDetails)){
            return R.ok(companyDetails);
        } else {
            return R.failed("该公司不存在");
        }
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @DeleteMapping("/removeComp")
    @ApiOperation("【管理员】删除公司账户")
    public R removeComp(@RequestParam("userId")Integer userId){
        return userService.removeComp(userId);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @GetMapping("/getAllCompany")
    @ApiOperation("【管理员】查询所有公司賬戶")
    public R getAllCompany(Page page, AdminPageDTO adminPageDTO){
        return userService.getAllCompany(page,adminPageDTO);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @GetMapping("/getAllStudio")
    @ApiOperation("【管理员】查询所有工作室賬戶")
    public R getAllStudio(Page page, AdminPageDTO adminPageDTO){
        return userService.getAllStudio(page,adminPageDTO);
    }
}
