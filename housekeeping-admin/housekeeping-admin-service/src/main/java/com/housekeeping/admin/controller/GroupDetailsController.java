package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.service.IGroupDetailsService;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Api(value="分組controller",tags={"【公司】分組管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/groupDetails")
public class GroupDetailsController {

    private final IGroupDetailsService groupDetailsService;

    @ApiOperation("新增分組,只允许公司账户进行操作")
    @PostMapping("/saveGroup")
    public R saveGroup(@RequestBody GroupDetailsDTO groupDetailsDTO){
        return groupDetailsService.saveGroup(groupDetailsDTO);
    }

    @ApiOperation("修改分組")
    @PostMapping("/updateGroup")
    public R updateGroup(@RequestBody GroupDetails group){
        group.setUpdateTime(LocalDateTime.now());
        return R.ok(groupDetailsService.updateById(group));
    }

    @ApiOperation("刪除分組")
    @DeleteMapping("/deleteGroup/{id}")
    public R deleteGroup(@PathVariable("id")Integer id){
        return groupDetailsService.cusRemove(id);
    }

    @ApiOperation("查看分組")
    @GetMapping("/getGroup")
    public R getGroup(Page page, Integer companyId, Integer id){
        return R.ok(groupDetailsService.getGroup(page,companyId, id));
    }

    @ApiOperation("分組新增經理")
    @PostMapping("/addMan")
    public R addMan(@RequestParam Integer groupId, @RequestParam Integer managerId){
        return groupDetailsService.addMan(groupId,managerId);
    }


}
