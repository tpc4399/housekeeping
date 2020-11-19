package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.Group;
import com.housekeeping.admin.service.GroupService;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Api(value="分組controller",tags={"【公司】分組管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    @ApiOperation("新增分組")
    @PostMapping("/saveGroup")
    public R saveGroup(@RequestBody Group group){
        group.setLastReviserId(TokenUtils.getCurrentUserId());
        group.setCreateTime(LocalDateTime.now());
        group.setUpdateTime(LocalDateTime.now());
        return R.ok(groupService.save(group));
    }

    @ApiOperation("修改分組")
    @PostMapping("/updateGroup")
    public R updateGroup(@RequestBody Group group){
        group.setUpdateTime(LocalDateTime.now());
        return R.ok(groupService.updateById(group));
    }

    @ApiOperation("刪除分組")
    @DeleteMapping("/deleteGroup/{id}")
    public R deleteGroup(@PathVariable("id")Integer id){
        return groupService.cusRemove(id);
    }

    @ApiOperation("查看分組")
    @GetMapping("/getGroup")
    public R getGroup(Page page, Integer companyId,Integer id){
        return R.ok(groupService.getGroup(page,companyId, id));
    }

    @ApiOperation("分組新增經理")
    @PostMapping("/addMan")
    public R addMan(@RequestParam Integer groupId,@RequestParam Integer managerId){
        return groupService.addMan(groupId,managerId);
    }


}
