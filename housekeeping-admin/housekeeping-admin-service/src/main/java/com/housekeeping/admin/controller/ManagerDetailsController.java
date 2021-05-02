package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.ManagerDetailsDTO;

import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.dto.PageOfManagerDetailsDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.service.IManagerMenuService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.admin.service.impl.GroupManagerServiceImpl;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.UnknownHostException;

@Api(value="經理controller",tags={"【经理详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/managerDetails")
public class ManagerDetailsController {

    private final ManagerDetailsService managerDetailsService;
    private final GroupManagerServiceImpl groupManagerService;
    private final IManagerMenuService managerMenuService;
    private final IUserService userService;

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】新增經理")
    @LogFlag(description = "新增經理")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody ManagerDetailsDTO managerDetailsDTO){
        return managerDetailsService.saveEmp(managerDetailsDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @ApiOperation("【管理员】【公司】【经理】修改經理信息")
    @LogFlag(description = "修改經理信息")
    @PostMapping("/updateEmp")
    public R updateEmp(@RequestBody ManagerDetailsDTO managerDetailsDTO){
        return managerDetailsService.updateEmp(managerDetailsDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【管理员】【公司】刪除經理")
    @LogFlag(description = "刪除經理")
    @DeleteMapping("/deleteEmp")
    public R deleteEmp(Integer managerId){
        return managerDetailsService.cusRemove(managerId);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】根据id生成登入参数")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h) throws UnknownHostException {
        return managerDetailsService.getLinkToLogin(id, h);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】查詢所有公司經理")
    @LogFlag(description = "查詢經理")
    @GetMapping("/page1")
    public R page1(Page page, PageOfManagerDTO pageOfEmployeesDTO){
        return managerDetailsService.cusPage1(page, pageOfEmployeesDTO, CommonConstants.REQUEST_ORIGIN_ADMIN);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】查詢该公司所有經理")
    @LogFlag(description = "查詢經理")
    @GetMapping("/page2")
    public R page2(Page page, PageOfManagerDetailsDTO pageOfEmployeesDetailsDTO){
        return managerDetailsService.cusPage(page, pageOfEmployeesDetailsDTO, CommonConstants.REQUEST_ORIGIN_COMPANY);
    }

    @Access({RolesEnum.USER_MANAGER})
    @ApiOperation("【经理】上传头像")
    @PostMapping("/uploadHead")
    public R uploadHead(@RequestParam("file") MultipartFile file) throws IOException {
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储head
        String fileName = managerDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        managerDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】上传經理头像")
    @PostMapping("/uploadHead2")
    public R uploadHead(@RequestParam("file") MultipartFile file,
                        @RequestParam("managerId") Integer managerId) throws IOException {
        Integer reviserId = managerDetailsService.getById(managerId).getId();
        //服务器存储head
        String fileName = managerDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        managerDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }


    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】根据公司的userId列出公司下面所有的经理")
    @GetMapping("/listHisManager/{companyUserId}")
    public R getAllByCompanyUserId(@PathVariable Integer companyUserId){
        return managerDetailsService.getAllByCompanyUserId(companyUserId);
    }

    @Access({RolesEnum.USER_MANAGER})
    @ApiOperation("【经理】查看自身信息")
    @GetMapping("getInfoById")
    public R getInfoById(){
        return managerDetailsService.getInfoById();
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】经理列表查看所有经理")
    @GetMapping("/getAllManagerByAdmin")
    public R getAllManagerByAdmin(Page page, PageOfManagerDTO pageOfEmployeesDTO){
        return managerDetailsService.getAllManagerByAdmin(page,pageOfEmployeesDTO);
    }
}
