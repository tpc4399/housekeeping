package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.vo.SysJobContendVo;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author su
 * @Date 2020/12/11 16:09
 */
@Api(tags={"【工作內容】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysJobContend")
public class SysJobContendController {

    private final ISysJobContendService sysJobContendService;

    @ApiOperation("根据ids获取树")
    @GetMapping("/getTreeByIds")
    public R getTreeByIds(Integer[] ids){
        return sysJobContendService.getTreeByIds(ids);
    }

    @ApiOperation("获取整树")
    @GetMapping("/getTree")
    public R getTree(){
        return sysJobContendService.getTree();
    }

    @ApiOperation("获取一级分类")
    @GetMapping("/getParents")
    public R getParents(){
        return sysJobContendService.getParents();
    }

    @PostMapping
    @ApiOperation("【管理员】增加工作内容")
    public R add(@RequestBody SysJobContendVo vo){
        return sysJobContendService.add(vo);
    }

    @PutMapping
    @ApiOperation("【管理员】设置整树")
    public R set(@RequestBody List<SysJobContendVo> vos){
        return sysJobContendService.set(vos);
    }

}
