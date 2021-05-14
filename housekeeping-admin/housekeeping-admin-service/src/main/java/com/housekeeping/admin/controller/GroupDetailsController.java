package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.service.IGroupDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Api(value="分組controller",tags={"【分組】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/groupDetails")
public class GroupDetailsController {

    private final IGroupDetailsService groupDetailsService;

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】新增分組,只允许公司账户进行操作")
    @PostMapping("/saveGroup")
    public R saveGroup(@RequestBody GroupDetailsDTO groupDetailsDTO){
        return groupDetailsService.saveGroup(groupDetailsDTO);
    }
    @Access({RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @PostMapping(value = "/addGroup2", headers = "content-type=multipart/form-data")
    @ApiOperation("【公司】【经理】新接口!新增分組")
    public R addGroup2(@RequestParam(value = "headPortrait",required = false) String headPortrait,
                       @RequestParam("name") String name,
                       @RequestParam("managerIds") Integer[] managerIds,
                       @RequestParam(value = "employeesIds",required = false) Integer[] employeesIds){
        return groupDetailsService.addGroup2(headPortrait, name, managerIds, employeesIds);
    }

    @Access({RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @PutMapping(value = "/updateGroup2", headers = "content-type=multipart/form-data")
    @ApiOperation("【公司】【经理】新接口!修改分組")
    public R updateGroup2(@RequestParam Integer groupId,
                          @RequestParam(value = "headPortrait",required = false) String headPortrait,
                          @RequestParam("name") String name,
                          @RequestParam("managerIds") Integer[] managerIds,
                          @RequestParam(value = "employeesIds",required = false) Integer[] employeesIds){
        return groupDetailsService.updateGroup2(groupId,headPortrait, name, managerIds, employeesIds);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】修改分組")
    @PostMapping("/updateGroup")
    public R updateGroup(@RequestBody GroupDetailsUpdateDTO groupDetailsUpdateDTO){
        return groupDetailsService.updateGroup(groupDetailsUpdateDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】【管理员】刪除分組")
    @DeleteMapping("/groupDetails/{id}")
    public R deleteGroupDetails(@PathVariable("id") Integer id){
        return groupDetailsService.cusRemove(id);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【管理员】【公司】查看分組")
    @GetMapping("/getGroup")
    public R getGroup(Page page, Integer companyId, Integer id){
        return R.ok(groupDetailsService.getGroup(page,companyId, id));
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】【管理员】分组上传logo")
    @LogFlag(description = "分组上传logo")
    @PostMapping("/uploadLogo")
    public R uploadLogo(@RequestParam("file") MultipartFile file,@RequestParam("groupId") Integer groupId) throws IOException {
        //服务器存储logo
        String fileName = groupDetailsService.uploadLogo(file, groupId);
        //数据库存储logoUrl
        groupDetailsService.updateLogUrlByGroupId(fileName, groupId);
        return R.ok("logo保存成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER})
    @ApiOperation("【all】分组返回数据")
    @LogFlag(description = "分组返回数据")
    @GetMapping("/getGroupData")
    public R getGroupData(Page page,Integer companyId, Integer id,String groupName){
        return groupDetailsService.getGroupData(page,companyId, id ,groupName);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】修改分組")
    @PostMapping("/updateGroupByAdmin")
    public R updateGroupByAdmin(@RequestParam("companyId")Integer companyId,
                                @RequestParam("groupName")String groupName,
                                @RequestParam("id")Integer id){
        return groupDetailsService.updateGroupByAdmin(id,groupName,companyId);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】新增分組")
    @PostMapping("/saveGroupByAdmin")
    public R saveGroupByAdmin(@RequestParam("companyId")Integer companyId,
                              @RequestParam("groupName")String groupName){
        return groupDetailsService.saveGroupByAdmin(companyId,groupName);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】分组列表获取所有分组")
    @GetMapping("/getAllGroups")
    public R getAllGroups(Page page,String groupName){
        return groupDetailsService.getAllGroups(page,groupName);
    }
}
