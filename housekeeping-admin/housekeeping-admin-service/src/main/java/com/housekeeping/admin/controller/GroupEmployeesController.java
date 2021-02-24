package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.GroupAdminDTO;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.service.IGroupEmployeesService;
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

    @ApiOperation("【公司】【管理员】分组增加刪除员工")
    @PostMapping
    public R save(@RequestBody GroupEmployeesDTO groupEmployeesDTO){
        return groupEmployeesService.save(groupEmployeesDTO);
    }

    @GetMapping("/getAllEmpByAdmin")
    @ApiOperation("【管理员】分状态获取公司及组下员工")
    public R getAllEmpByAdmin(GroupAdminDTO groupDTO){
        return groupEmployeesService.getAllEmpByAdmin(groupDTO);
    }

    @GetMapping("/getAllEmp")
    @ApiOperation("【公司】分状态获取公司及组下员工")
    public R getAllEmp(GroupDTO groupDTO){
        return groupEmployeesService.getAllEmp(groupDTO);
    }

    @GetMapping("/getAllEmpById")
    @ApiOperation("【管理员】【公司】通过组id获取组下所有员工")
    public R getAllEmpById(Integer groupId){
        return groupEmployeesService.getAllEmpById(groupId);
    }
}
