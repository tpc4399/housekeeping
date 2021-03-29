package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.service.ISysIndexService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags={"【首页】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/index")
public class SysIndexController {

    private final ISysIndexService sysIndexService;

    @GetMapping
    @ApiOperation("获取所有分类")
    public R getAll(){
        return sysIndexService.getAll();
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping
    @ApiOperation("【平台】新增分类")
    public R add(@RequestBody SysIndexAddDto sysIndexAddDto){
        return sysIndexService.add(sysIndexAddDto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PutMapping
    @ApiOperation("【平台】修改分类")
    public R update(@RequestBody SysIndexUpdateDTO dto){
        return sysIndexService.update(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【平台】删除分类")
    @DeleteMapping("/{indexId}")
    public R delete(@PathVariable Integer indexId){
        return sysIndexService.delete(indexId);
    }

    @GetMapping("/getById")
    @ApiOperation("通过首页大类获取下面工作内容标签")
    public R getById(Integer id){
        return sysIndexService.getCusById(id);
    }

    @ApiOperation("主页搜索")
    @PostMapping("/query")
    public R query(@RequestBody QueryIndexDTO dto) throws InterruptedException {
        return sysIndexService.query(dto);
    }

    @GetMapping("/tree")
    @ApiOperation("获取六大类包括类下的工作内容")
    public R tree(){
        return sysIndexService.tree();
    }

    @ApiOperation("获取默认推荐,如果没有地址，body直接置null")
    @PostMapping("/defaultRecommendation")
    public R defaultRecommendation(@RequestBody AddressDTO dto){
        return sysIndexService.defaultRecommendation(dto);
    }

}
