package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.GroupAdminDTO;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.service.IGroupManagerService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/25 14:58
 */
@Api(value="分組controller",tags={"【分組&经理】中間表接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/groupManager")
public class GroupManagerController {

    private final IGroupManagerService groupManagerService;

    @ApiOperation("【公司】【管理员】分组增加删除经理")
    @PostMapping
    public R save(@RequestBody GroupManagerDTO groupManagerDTO){
        return groupManagerService.save(groupManagerDTO);
    }

    @GetMapping("/getAllMan")
    @ApiOperation("【公司】分状态获取公司及组下经理")
    public R getAllEmp(GroupDTO groupDTO){
        return groupManagerService.getAllMan(groupDTO);
    }

    @GetMapping("/getAllManByAdmin")
    @ApiOperation("【管理员】分状态获取公司及组下经理")
    public R getAllManByAdmin(GroupAdminDTO groupDTO){
        return groupManagerService.getAllManByAdmin(groupDTO);
    }

    @GetMapping("/getAllManById")
    @ApiOperation("【公司】【管理员】通过组id获取组下所有经理")
    public R getAllManById(Integer groupId){
        return groupManagerService.getAllManById(groupId);
    }
}
