package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.housekeeping.admin.service.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Api(value="分組controller",tags={"分組管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    @ApiOperation("新增分組(分組名、經理id、多個員工id)")
    @PostMapping("/saveGroup")
    public R saveGroup(@RequestBody Map map){
        return R.ok(groupService.saveGroup(map));
    }

    @ApiOperation("刪除分組")
    @DeleteMapping("/deleteGroup")
    public R deleteGroup(Integer groupManagerId){
        return R.ok(groupService.cusRemove(groupManagerId));
    }


}
