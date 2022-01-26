package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.AdminPageDTO;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.RegisterCompanyDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.EmployeesWorkExperience;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IEmployeesWorkExperienceService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.admin.vo.EmployeesDetailsWorkVo;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Api(tags={"【个体户】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/personal")
public class PersonalController {

    private final IUserService userService;
    private final EmployeesDetailsService employeesDetailsService;
    private final IEmployeesWorkExperienceService employeesWorkExperienceService;
    private final ICompanyDetailsService companyDetailsService;
    /**
     *
     * @param data
     * @param type 1 手机号 2 邮箱
     * @return
     */
    @ApiOperation("异步检测个体户手机号或者邮箱是否重复(1 手机号 2 邮箱)")
    @GetMapping("/checkPersonal/{data}/{type}")
    public R checkDataPersonal(@PathVariable("data")String data, @PathVariable("type")String type){
        R r = this.userService.checkData2(data, type);
        return r;
    }


    @ApiOperation("个体户注册1发送验证码")
    @LogFlag(description = "手機號注册獲取驗證碼")
    @GetMapping("/personalSMS")
    public R registerB(@RequestParam("phonePrefix") String phonePrefix,
                       @RequestParam("phone") String phone) throws Exception {
        return userService.sendRegisterMSMessage(phonePrefix,phone,6);
    }

    @ApiOperation("个体户注册2发送注册信息")
    @LogFlag(description = "注册个体户账号")
    @PostMapping("/savePersonal")
    public R saveEmp(@RequestBody RegisterCompanyDTO dto){
        return userService.savePersonal(dto);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @GetMapping("/getAllPersonal")
    @ApiOperation("【管理员】查询所有个体户賬戶")
    public R getAllCompany(Page page, AdminPageDTO adminPageDTO){
        return userService.getAllUser(page,adminPageDTO,6);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @DeleteMapping("/removePersonal")
    @ApiOperation("【管理员】删除个体户账户")
    public R removeComp(@RequestParam("userId")Integer userId){
        return userService.removePersonal(userId);
    }

    @ApiOperation("修改个体户信息")
    @LogFlag(description = "修改个体户信息")
    @PostMapping("/updatePersonal")
    public R updateEmp(@RequestBody EmployeesDetailsDTO employeesDetailsDTO) throws ParseException {
        return employeesDetailsService.updateEmp(employeesDetailsDTO);
    }

    @ApiOperation("获取个人信息")
    @GetMapping("/getInfo")
    public R getInfo(){
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", currentUserId);

        CompanyDetails one1 = companyDetailsService.getOne(qw);

        EmployeesDetails one = employeesDetailsService.getOne(qw);

        EmployeesDetailsWorkVo employeesDetailsWorkVo = new EmployeesDetailsWorkVo();

        QueryWrapper<EmployeesWorkExperience> qw2 = new QueryWrapper<>();
        qw2.eq("employees_id",one.getId());
        List<EmployeesWorkExperience> list = employeesWorkExperienceService.list(qw2);

        employeesDetailsWorkVo.setCompanyDetails(one1);
        employeesDetailsWorkVo.setEmployeesDetails(one);
        employeesDetailsWorkVo.setEmployeesWorkExperiences(list);
        employeesDetailsWorkVo.setDeptId(userService.getById(currentUserId).getDeptId());
        return R.ok(employeesDetailsWorkVo);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @GetMapping("/getPersonal")
    @ApiOperation("【管理员】查询所有个体户")
    public R getPersonal(Page page, PageOfEmployeesDTO pageOfEmployeesDTO){
        return employeesDetailsService.getPersonal(page,pageOfEmployeesDTO);
    }

    /*@ApiOperation("管理员添加个体化账户")
    @LogFlag(description = "管理员添加个体化账户")
    @PostMapping("/savePersonalByAdmin")
    public R savePersonalByAdmin(@RequestBody RegisterPersonalDTO dto){
        return userService.savePersonalByAdmin(dto);
    }*/

}
