package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.service.IGroupDetailsService;
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

    @ApiOperation("【公司】新增分組,只允许公司账户进行操作")
    @PostMapping("/saveGroup")
    public R saveGroup(@RequestBody GroupDetailsDTO groupDetailsDTO){
        return groupDetailsService.saveGroup(groupDetailsDTO);
    }

    @ApiOperation("【公司】修改分組")
    @PostMapping("/updateGroup")
    public R updateGroup(@RequestBody GroupDetailsUpdateDTO groupDetailsUpdateDTO){
        return groupDetailsService.updateGroup(groupDetailsUpdateDTO);
    }

    @ApiOperation("【公司】刪除分組")
    @DeleteMapping("/groupDetails/{id}")
    public R deleteGroupDetails(@PathVariable("id") Integer id){
        return groupDetailsService.cusRemove(id);
    }

    @ApiOperation("【公司】查看分組")
    @GetMapping("/getGroup")
    public R getGroup(Page page, Integer companyId, Integer id){
        return R.ok(groupDetailsService.getGroup(page,companyId, id));
    }

    @ApiOperation("【公司】分组上传logo")
    @LogFlag(description = "分组上传logo")
    @PostMapping("/uploadLogo")
    public R uploadLogo(@RequestParam("file") MultipartFile file,@RequestParam("groupId") Integer groupId) throws IOException {
        //服务器存储logo
        String fileName = groupDetailsService.uploadLogo(file, groupId);
        //数据库存储logoUrl
        groupDetailsService.updateLogUrlByGroupId(fileName, groupId);
        return R.ok("logo保存成功");
    }

    @ApiOperation("分组返回数据")
    @LogFlag(description = "分组返回数据")
    @GetMapping("/getGroupData")
    public R getGroupData(Integer companyId, Integer id,String groupName){
        return groupDetailsService.getGroupData(companyId, id ,groupName);
    }


}
