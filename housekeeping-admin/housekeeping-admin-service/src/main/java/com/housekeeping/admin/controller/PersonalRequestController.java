package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.PersonalRequest;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.admin.service.PersonalRequestService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Api(tags={"【個體戶申請升級工作室】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/personalRequest")
public class PersonalRequestController {

    private final PersonalRequestService personalRequestService;
    private final EmployeesDetailsService employeesDetailsService;
    private final ICompanyDetailsService companyDetailsService;
    private final IUserService userService;

    @PostMapping
    @ApiOperation("個體戶發送申請接口")
    public R save(@RequestBody PersonalRequest personalRequest){

        Integer personalId = personalRequest.getPersonalId();
        QueryWrapper<PersonalRequest> qw = new QueryWrapper<>();
        qw.eq("personal_id",personalId);
        List<PersonalRequest> list = personalRequestService.list(qw);
        if(CollectionUtils.isNotEmpty(list)){
            return R.failed("已存在申請，請勿重複提交");
        }

        EmployeesDetails employeesDetails = employeesDetailsService.getById(personalId);
        CompanyDetails companyDetails = companyDetailsService.getById(employeesDetails.getCompanyId());
        User user = userService.getById(companyDetails.getUserId());
        QueryWrapper<User> qw2 = new QueryWrapper<>();
        qw2.eq("phone",user.getPhone());
        qw2.eq("dept_id",2);
        List<User> list1 = userService.list(qw2);
        if(CollectionUtils.isNotEmpty(list1)){
            return R.failed("該手機號已存在相應工作室，無法再申請升級為工作室");
        }

        personalRequest.setCreateTime(LocalDateTime.now());
        personalRequest.setStatus(0);
        personalRequestService.save(personalRequest);
        return R.ok("發送申請成功");
    }

    @PostMapping("/update")
    @ApiOperation("個體戶修改申请，重新提交")
    public R update(@RequestBody PersonalRequest personalRequest){

        PersonalRequest byId = personalRequestService.getById(personalRequest.getId());
        byId.setCreateTime(LocalDateTime.now());
        byId.setStatus(0);
        byId.setPersonalReason(personalRequest.getPersonalReason());
        byId.setType(personalRequest.getType());
        byId.setCompanyName(personalRequest.getCompanyName());
        byId.setCompanyNumber(personalRequest.getCompanyNumber());
        byId.setLegalName(personalRequest.getLegalName());
        byId.setPhonePrefix(personalRequest.getPhonePrefix());
        byId.setPhone(personalRequest.getPhone());
        byId.setRegisterAddress(personalRequest.getRegisterAddress());
        byId.setEnclosure(personalRequest.getEnclosure());
        personalRequestService.updateById(personalRequest);
        return R.ok("發送申請成功");
    }

    @ApiOperation("個體戶查看申請")
    @GetMapping("/getByPersonal")
    public R getByPersonal(Integer personId){
        QueryWrapper<PersonalRequest> qw = new QueryWrapper<>();
        qw.eq("personal_id",personId);
        PersonalRequest one = personalRequestService.getOne(qw);
        return R.ok(one);
    }

    @ApiOperation("個體戶取消申請")
    @DeleteMapping
    public R remove(Integer personId){
        QueryWrapper<PersonalRequest> qw = new QueryWrapper<>();
        qw.eq("personal_id",personId);
        return R.ok(personalRequestService.remove(qw));
    }

    @ApiOperation("管理員查看所有申請")
    @GetMapping("/getAll")
    public R getAll(Page page,Integer id,Integer status,String name,Integer type){
        return personalRequestService.getAll(page,id,status,name,type);
    }

    @ApiOperation("管理員拒絕申請")
    @GetMapping("/refuse")
    public R refuse(@RequestParam Integer id,String reason){
        PersonalRequest byId = personalRequestService.getById(id);
        byId.setStatus(1);
        byId.setUpdateTime(LocalDateTime.now());
        byId.setAdminReason(reason);
        personalRequestService.updateById(byId);
        return R.ok("成功拒絕");
    }

    @ApiOperation("管理員同意申請")
    @GetMapping("/agree")
    public R agree(@RequestParam Integer id,String reason){
        PersonalRequest byId = personalRequestService.getById(id);
        byId.setStatus(2);
        byId.setUpdateTime(LocalDateTime.now());
        byId.setAdminReason(reason);
        personalRequestService.updateById(byId);

        EmployeesDetails employeesDetails = employeesDetailsService.getById(byId.getPersonalId());
        CompanyDetails companyDetails = companyDetailsService.getById(employeesDetails.getCompanyId());

        //工作室
        if(byId.getType().equals(1)){
            personalRequestService.updateCompany(employeesDetails.getCompanyId());
        }
        //公司
        else{
            personalRequestService.updateCompany2(employeesDetails.getCompanyId());
        }

        User user = userService.getById(companyDetails.getUserId());
        user.setDeptId(2);
        userService.updateById(user);

        return R.ok("成功同意");
    }


}
