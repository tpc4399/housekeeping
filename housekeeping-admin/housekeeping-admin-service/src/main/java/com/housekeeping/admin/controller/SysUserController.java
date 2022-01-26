package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @Date 2021/2/7 15:47
 */
@Api(tags={"【用户管理】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysUser")
public class SysUserController {

    private final IUserService userService;
    private final ICustomerDetailsService customerDetailsService;
    private final EmployeesDetailsService employeesDetailsService;

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/page")
    @ApiOperation("【管理员】分页查询用户")
    public R page(Page page, PageOfUserDTO dto){
        return userService.page(page, dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping("/addAccount1")
    @ApiOperation("【管理员】添加管理员、公司、家庭、个体户账户接口")
    public R add1(@RequestBody AdminAdd1DTO dto){
        return userService.add1(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping("/updateAccount1")
    @ApiOperation("【管理员】修改管理员、公司、家庭账户接口")
    public R update1(@RequestBody AdminUpdate1DTO dto){
        return userService.update1(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping("/addAccount2")
    @ApiOperation("【管理员】添加经理、保洁员账户接口")
    public R add2(@RequestBody AdminAdd2DTO dto){
        return userService.add2(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping("/updateAccount2")
    @ApiOperation("【管理员】修改经理、保洁员账户信息接口")
    public R updateAccount2(@RequestBody AdminUpdate2DTO dto){
        return userService.update2(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】黑名单操作 roleType:12345分别为 管理员 公司 家庭 经理 保洁员  id:为他们自己的id而非userId， 目前仅支持拉黑家庭和保洁员  action：true代表拉黑操作，false代表从移出黑名单")
    @PutMapping("/blacklist")
    public R blacklist(Integer roleType, Integer id, Boolean action){
        if (roleType.equals(3)){
            return customerDetailsService.blacklist(id, action);
        }else if (roleType.equals(5)){
            return employeesDetailsService.blacklist(id, action);
        }else {
            return R.failed("參數不對勁");
        }
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/getAllBlackEmp")
    @ApiOperation("【管理员】查询黑名单保洁员")
    public R blackEmplist(){
        QueryWrapper qw = new QueryWrapper<EmployeesDetails>();
        qw.eq("blacklist_flag",1);
        List<EmployeesDetails> list = employeesDetailsService.list(qw);
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            User byId = userService.getById(list.get(i).getUserId());
            users.add(byId);
        }
        return R.ok(users);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/getAllBlackCus")
    @ApiOperation("【管理员】查询黑名单客户")
    public R blackCuslist(){
        QueryWrapper qw = new QueryWrapper<CustomerDetails>();
        qw.eq("blacklist_flag",1);
        List<CustomerDetails> list = customerDetailsService.list(qw);
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            User byId = userService.getById(list.get(i).getUserId());
            users.add(byId);
        }
        return R.ok(users);
    }

    @GetMapping("/getDeptId")
    @ApiOperation("根据当前用户返回部门id")
    public R getDeptId(){
        User byId = userService.getById(TokenUtils.getCurrentUserId());
        return R.ok(byId.getDeptId());
    }

    @GetMapping("/getSms")
    @ApiOperation("獲取短信列表（注冊/登入）")
    public R getSms(Integer type){
        return userService.getSms(type);
    }

    @ApiOperation("忘记密码1发送验证码")
    @LogFlag(description = "忘記密碼")
    @GetMapping("/forgetSMS")
    public R empForgetSMS(@RequestParam("phonePrefix") String phonePrefix,
                          @RequestParam("phone") String phone,
                          Integer deptId) throws Exception {
        return userService.sendForgetSms(phonePrefix,phone,deptId);
    }

    @ApiOperation("忘记密码2校验验证码")
    @GetMapping("/verfifyPersonalCode")
    public R verfifyAdminCode(@RequestParam("phonePrefix") String phonePrefix,
                              @RequestParam("phone") String phone,
                              @RequestParam("code")String code){
        return userService.verfifyCode2(phonePrefix, phone,code);
    }

    @ApiOperation("忘记密码3发送新密码")
    @LogFlag(description = "忘記密碼,修改密碼")
    @PostMapping("/updatePwd")
    public R empUpdatePwd(@RequestBody ForgetDTO forgetDTO){
        return userService.updatePwdByPhone(forgetDTO);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public R getInfo(){
        return userService.getInfo();
    }

}
