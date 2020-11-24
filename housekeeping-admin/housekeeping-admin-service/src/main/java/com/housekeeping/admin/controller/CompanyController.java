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

@Api(value="公司人員controller",tags={"【公司】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyHuman")
public class CompanyController {

    private final IUserService userService;

    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测公司手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkEmp/{data}/{type}")
    public R checkDataEmp(@PathVariable("data")String data, @PathVariable("type")Integer type){
        R r = this.userService.checkData(2, data, type);
        return r;
    }


    @ApiOperation("【公司人员注册】发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/EmpSMS")
    public R registerB(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone){
        return userService.sendRegisterMSMessage(phonePrefix,phone,2);
    }

    @ApiOperation("【注册】公司注册")
    @LogFlag(description = "注册公司账号")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody RegisterDTO registerDTO){
        return userService.saveEmp(registerDTO);
    }

    @ApiOperation("【公司人员忘記密碼】发送验证码")
    @LogFlag(description = "公司人员忘記密碼")
    @GetMapping("/EmpForgetSMS")
    public R empForgetSMS(@RequestParam("phonePrefix") String phonePrefix,
                          @RequestParam("phone") String phone){
        return userService.sendForgetMSMessage(phonePrefix,phone,2);
    }

    @ApiOperation("公司忘記密碼,修改密碼")
    @LogFlag(description = "公司忘記密碼,修改密碼")
    @PostMapping("/EmpUpdatePwd")
    public R empUpdatePwd(@RequestBody ForgetDTO forgetDTO){
        forgetDTO.setDeptId(2);
        return userService.updatePwd(forgetDTO);
    }

    @ApiOperation("校验验证码")
    @GetMapping("/verfifyCompanyCode")
    public R verfifyAdminCode(@RequestParam("phonePrefix") String phonePrefix,
                              @RequestParam("phone") String phone,
                              @RequestParam("code")String code){
        return userService.verfifyCode(phonePrefix, phone,code,2);
    }

}
