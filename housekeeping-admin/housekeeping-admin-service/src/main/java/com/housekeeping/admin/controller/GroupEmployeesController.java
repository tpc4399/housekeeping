package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.GroupAdminDTO;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupEmployeesAdminDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.service.IGroupEmployeesService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/25 14:58
 */
@Api(value="分組controller",tags={"【分組&员工】中間表接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/groupEmployees")
public class GroupEmployeesController {

    private final IGroupEmployeesService groupEmployeesService;

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】分组增加刪除员工")
    @PostMapping
    public R save(@RequestBody GroupEmployeesDTO groupEmployeesDTO){
        return groupEmployeesService.save(groupEmployeesDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】分组增加刪除员工")
    @PostMapping("/saveByAdmin")
    public R saveByAdmin(@RequestBody GroupEmployeesAdminDTO groupEmployeesDTO){
        return groupEmployeesService.saveByAdmin(groupEmployeesDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/getAllEmpByAdmin")
    @ApiOperation("【管理员】分状态获取公司及组下员工")
    public R getAllEmpByAdmin(GroupAdminDTO groupDTO){
        return groupEmployeesService.getAllEmpByAdmin(groupDTO);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @GetMapping("/getAllEmp")
    @ApiOperation("【公司】【经理】分状态获取公司及组下员工")
    public R getAllEmp(GroupDTO groupDTO){
        return groupEmployeesService.getAllEmp(groupDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @GetMapping("/getAllEmpById")
    @ApiOperation("【管理员】【公司】【经理】通过组id获取组下所有员工")
    public R getAllEmpById(Integer groupId){
        return R.ok(groupEmployeesService.getAllEmpById(groupId));
    }
}
